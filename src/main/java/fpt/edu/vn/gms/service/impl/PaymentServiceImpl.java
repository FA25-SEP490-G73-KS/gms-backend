package fpt.edu.vn.gms.service.impl;

import fpt.edu.vn.gms.dto.response.PaymentDetailResDto;
import fpt.edu.vn.gms.dto.response.PaymentListResDto;
import fpt.edu.vn.gms.entity.*;
import fpt.edu.vn.gms.exception.ResourceNotFoundException;
import fpt.edu.vn.gms.mapper.PaymentMapper;
import fpt.edu.vn.gms.repository.DebtRepository;
import fpt.edu.vn.gms.repository.PaymentRepository;
import fpt.edu.vn.gms.repository.PriceQuotationRepository;
import fpt.edu.vn.gms.repository.ServiceTicketRepository;
import fpt.edu.vn.gms.service.CodeSequenceService;
import fpt.edu.vn.gms.service.PaymentService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    ServiceTicketRepository serviceTicketRepo;
    PriceQuotationRepository priceQuotationRepo;
    PaymentRepository paymentRepo;
    DebtRepository debtRepo;
    CodeSequenceService codeSequenceService;
    PaymentMapper mapper;

    @Override
    @Transactional
    public void createPayment(Long serviceTicketId, Long quotationId) {

        // Lấy service ticket
        ServiceTicket serviceTicket = serviceTicketRepo.findById(serviceTicketId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phiếu dịch vụ: " + serviceTicketId));

        // Lấy báo giá
        PriceQuotation priceQuotation = priceQuotationRepo.findById(quotationId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy báo giá: " + quotationId));

        // Lấy Customer từ Service Ticket
        Customer customer = serviceTicket.getCustomer();
        if (customer == null) {
            throw new ResourceNotFoundException("Phiếu dịch vụ chưa gắn khách hàng!");
        }

        BigDecimal itemTotal = priceQuotation.getEstimateAmount() != null ? priceQuotation.getEstimateAmount() : BigDecimal.ZERO;
        BigDecimal laborCost = priceQuotation.getLaborCost() != null ? priceQuotation.getLaborCost() : BigDecimal.ZERO;
        BigDecimal discountRate = customer.getDiscountPolicy() != null && customer.getDiscountPolicy().getDiscountRate() != null
                ? customer.getDiscountPolicy().getDiscountRate()
                : BigDecimal.ZERO;

        BigDecimal discount = itemTotal
                .add(laborCost)
                .multiply(discountRate)
                .divide(BigDecimal.valueOf(100));

        // Lấy tiền cọc (nếu không có cọc -> 0)
        BigDecimal depositAmount = BigDecimal.ZERO;

        // Lấy công nợ cũ = tổng các payment chưa thanh toán hết
        BigDecimal previousDebt = debtRepo.getTotalDebt(customer.getCustomerId());

        // Tổng số tiền cần trả = hàng + công - giảm giá - cọc + công nợ cũ
        BigDecimal amountPaid = itemTotal
                .add(laborCost)
                .subtract(discount)
                .subtract(depositAmount)
                .add(previousDebt);

        // Tạo payment
        Payment payment = Payment.builder()
                .code(codeSequenceService.generateCode("PAY"))
                .serviceTicket(serviceTicket)
                .quotation(priceQuotation)
                .itemTotal(itemTotal)
                .laborCost(laborCost)
                .discount(discount)
                .depositReceived(depositAmount)
                .previousDebt(previousDebt)
                .finalAmount(amountPaid)
                .currency("VND")
                .createdBy("hệ thống")
                .createdAt(LocalDateTime.now())
                .build();

        paymentRepo.save(payment);
    }

    @Override
    public Page<PaymentListResDto> getPaymentList(int page, int size, String sort) {

        log.info("Fetching payment list with paging: page={}, size={}, sort={}", page, size, sort);

        String[] sortParams = sort.split(",");
        Sort.Direction direction = Sort.Direction.fromString(sortParams[1]);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortParams[0]));

        Page<Payment> payments = paymentRepo.findAllWithRelations(pageable);

        return payments.map(payment -> {
            PaymentListResDto dto = mapper.toListDto(payment);

            Long customerId = payment.getServiceTicket().getCustomer().getCustomerId();
            BigDecimal totalDebt = debtRepo.getTotalDebt(customerId);

            dto.setPreviousDebt(totalDebt);

            return dto;
        });
    }

    @Override
    public PaymentDetailResDto getPaymentDetail(Long paymentId) {

        log.info("Fetching payment detail, paymentId={}", paymentId);

        Payment payment = paymentRepo.findById(paymentId)
                .orElseThrow(() -> {
                    log.warn("Payment not found, id={}", paymentId);
                    return new ResourceNotFoundException("Không tìm thấy phiếu thanh toán!");
                });

        return mapper.toDetailDto(payment);
    }
}
