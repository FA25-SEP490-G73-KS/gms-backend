package fpt.edu.vn.gms.service.impl;

import fpt.edu.vn.gms.entity.Customer;
import fpt.edu.vn.gms.entity.Payment;
import fpt.edu.vn.gms.entity.PriceQuotation;
import fpt.edu.vn.gms.entity.ServiceTicket;
import fpt.edu.vn.gms.exception.ResourceNotFoundException;
import fpt.edu.vn.gms.repository.PaymentRepository;
import fpt.edu.vn.gms.repository.PriceQuotationRepository;
import fpt.edu.vn.gms.repository.ServiceTicketRepository;
import fpt.edu.vn.gms.service.PaymentService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PaymentServiceImpl implements PaymentService {

    ServiceTicketRepository serviceTicketRepo;
    PriceQuotationRepository priceQuotationRepo;
    PaymentRepository paymentRepo;

    @Override
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
        BigDecimal previousDebt = paymentRepo.sumUnpaidByCustomer(customer.getCustomerId())
                .orElse(BigDecimal.ZERO);

        // Tổng số tiền cần trả = hàng + công - giảm giá - cọc + công nợ cũ
        BigDecimal amountPaid = itemTotal
                .add(laborCost)
                .subtract(discount)
                .subtract(depositAmount)
                .add(previousDebt);

        // Tạo payment

        Payment payment = Payment.builder()
                .serviceTicket(serviceTicket)
                .quotation(priceQuotation)
                .itemTotal(itemTotal)
                .laborCost(laborCost)
                .discount(discount)
                .depositReceived(depositAmount)
                .previousDebt(previousDebt)
                .finalAmount(amountPaid)
                .paymentMethod(null)        // vì khách chưa thanh toán
                .paymentType(Payment.PaymentType.PAYMENT)
                .currency("VND")
                .createdBy("hệ thống")
                .createdAt(LocalDateTime.now())
                .build();

        paymentRepo.save(payment);
    }
}
