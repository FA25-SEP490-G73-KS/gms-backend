package fpt.edu.vn.gms.service.impl;

import fpt.edu.vn.gms.common.enums.DebtStatus;
import fpt.edu.vn.gms.common.enums.InvoiceStatus;
import fpt.edu.vn.gms.common.enums.PaymentTransactionType;
import fpt.edu.vn.gms.dto.request.PayInvoiceRequestDto;
import fpt.edu.vn.gms.common.enums.TransactionMethod;
import fpt.edu.vn.gms.dto.response.TransactionResponseDto;
import fpt.edu.vn.gms.dto.request.CreateTransactionRequestDto;
import fpt.edu.vn.gms.dto.response.CustomerDebtResponseDto;
import fpt.edu.vn.gms.dto.response.InvoiceDetailResDto;
import fpt.edu.vn.gms.dto.response.InvoiceListResDto;
import fpt.edu.vn.gms.entity.*;
import fpt.edu.vn.gms.exception.PaymentNotFoundException;
import fpt.edu.vn.gms.exception.ResourceNotFoundException;
import fpt.edu.vn.gms.mapper.CustomerDebtMapper;
import fpt.edu.vn.gms.mapper.InvoiceMapper;
import fpt.edu.vn.gms.repository.*;
import fpt.edu.vn.gms.service.CodeSequenceService;
import fpt.edu.vn.gms.service.CustomerService;
import fpt.edu.vn.gms.service.InvoiceService;
import fpt.edu.vn.gms.service.TransactionService;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class InvoiceServiceImpl implements InvoiceService {

        ServiceTicketRepository serviceTicketRepo;
        PriceQuotationRepository priceQuotationRepo;
        InvoiceRepository invoiceRepo;
        DebtRepository debtRepo;
        TransactionRepository transactionRepo;
        TransactionService transactionService;
        CodeSequenceService codeSequenceService;
        CustomerDebtMapper customerDebtMapper;
        InvoiceMapper mapper;
        CustomerService customerService;

        @Override
        @Transactional
        public void createInvoice(Long serviceTicketId, Long quotationId) {

                // Lấy service ticket
                ServiceTicket serviceTicket = serviceTicketRepo.findById(serviceTicketId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Không tìm thấy phiếu dịch vụ: " + serviceTicketId));

                // Lấy báo giá
                PriceQuotation priceQuotation = priceQuotationRepo.findById(quotationId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Không tìm thấy báo giá: " + quotationId));

                // Lấy Customer từ Service Ticket
                Customer customer = serviceTicket.getCustomer();
                if (customer == null) {
                        throw new ResourceNotFoundException("Phiếu dịch vụ chưa gắn khách hàng!");
                }

                BigDecimal itemTotal = priceQuotation.getEstimateAmount() != null ? priceQuotation.getEstimateAmount()
                                : BigDecimal.ZERO;
                BigDecimal discountRate = customer.getDiscountPolicy() != null
                                && customer.getDiscountPolicy().getDiscountRate() != null
                                                ? customer.getDiscountPolicy().getDiscountRate()
                                                : BigDecimal.ZERO;

                System.out.println(discountRate);

                BigDecimal discount = itemTotal
                                .multiply(discountRate)
                                .divide(BigDecimal.valueOf(100));

                // Lấy tiền cọc (nếu không có cọc -> 0)
                BigDecimal depositAmount = BigDecimal.ZERO;

                // Lấy công nợ cũ = tổng các payment chưa thanh toán hết
                BigDecimal previousDebt = debtRepo.getTotalDebt(customer.getCustomerId());

                // Tổng số tiền cần trả = hàng + công - giảm giá - cọc + công nợ cũ
                BigDecimal amountPaid = itemTotal
                                .subtract(discount)
                                .subtract(depositAmount)
                                .add(previousDebt);

                // Tạo payment
                invoiceRepo.save(Invoice.builder()
                                .code(codeSequenceService.generateCode("HD"))
                                .serviceTicket(serviceTicket)
                                .quotation(priceQuotation)
                                .depositReceived(depositAmount)
                                .finalAmount(amountPaid)
                                .createdBy("hệ thống")
                                .createdAt(LocalDateTime.now())
                                .build());

        }

        @Override
        public Page<InvoiceListResDto> getInvoiceList(int page, int size, String sort) {

                log.info("Fetching payment list with paging: page={}, size={}, sort={}", page, size, sort);

                String[] sortParams = sort.split(",");
                Sort.Direction direction = Sort.Direction.fromString(sortParams[1]);
                Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortParams[0]));

                Page<Invoice> payments = invoiceRepo.findAllWithRelations(pageable);

                return payments.map(mapper::toListDto);
        }

        @Override
        public InvoiceDetailResDto getInvoiceDetail(Long paymentId) {

                log.info("Fetching payment detail, paymentId={}", paymentId);

                Invoice payment = invoiceRepo.findById(paymentId)
                                .orElseThrow(() -> {
                                        log.warn("Payment not found, id={}", paymentId);
                                        return new ResourceNotFoundException("Không tìm thấy phiếu thanh toán!");
                                });

                InvoiceDetailResDto dto = mapper.toDetailDto(payment);

                BigDecimal paidAmount = transactionRepo.findByInvoiceAndIsActiveTrue(payment).stream()
                                .map(Transaction::getAmount)
                                .filter(Objects::nonNull)
                                .map(BigDecimal::valueOf)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);

                dto.setPaidAmount(paidAmount);

                return dto;
        }

        @Override
        @Transactional
        public CustomerDebtResponseDto createDebtFromInvoice(Long paymentId, LocalDate dueDate) {
                log.info("Creating debt from paymentId={} with dueDate={}", paymentId, dueDate);

                Invoice payment = invoiceRepo.findById(paymentId)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Không tìm thấy phiếu thanh toán: " + paymentId));

                ServiceTicket serviceTicket = payment.getServiceTicket();
                if (serviceTicket == null || serviceTicket.getCustomer() == null) {
                        throw new ResourceNotFoundException(
                                        "Phiếu thanh toán chưa gắn khách hàng / phiếu dịch vụ hợp lệ");
                }

                Customer customer = serviceTicket.getCustomer();

                // Tổng phải thu từ payment (finalAmount đã bao gồm công nợ cũ + giảm giá + cọc)
                BigDecimal totalAmount = payment.getFinalAmount() != null
                                ? payment.getFinalAmount()
                                : BigDecimal.ZERO;

                // Công nợ mới = tổng phải thu - số tiền đã thu
                BigDecimal newDebtAmount = totalAmount;
                if (newDebtAmount.compareTo(BigDecimal.ZERO) < 0) {
                        // không cho âm, log để dễ debug
                        log.warn("Computed newDebtAmount < 0 for paymentId={} (total={}), force to 0",
                                        paymentId, totalAmount);
                        newDebtAmount = BigDecimal.ZERO;
                }

                if (newDebtAmount.compareTo(BigDecimal.ZERO) == 0) {
                        log.info("Payment {} has no remaining amount, skip creating debt", paymentId);
                        return null;
                }

                if (dueDate == null) {
                        // Nếu FE không gửi dueDate, mặc định +30 ngày
                        dueDate = LocalDate.now().plusDays(30);
                }

                // Nếu không còn nợ -> có thể không tạo Debt, tuỳ nghiệp vụ
                DebtStatus status = DebtStatus.OUTSTANDING;

                Debt debt = Debt.builder()
                                .customer(customer)
                                .serviceTicket(serviceTicket)
                                .amount(newDebtAmount)
                                .paidAmount(BigDecimal.ZERO) // đây là nợ mới, chưa thu gì
                                .status(status)
                                .dueDate(dueDate)
                                .build();

                debt = debtRepo.save(debt);

                log.info("Created debt id={} for customer={} paymentId={} amount={} dueDate={}",
                                debt.getId(), customer.getCustomerId(), paymentId, newDebtAmount, dueDate);

                CustomerDebtResponseDto dto = customerDebtMapper.toDto(debt);
                // remaining = amount - paidAmount (hiện bằng amount)
                dto.setTotalAmount(newDebtAmount);
                dto.setPaidAmount(debt.getPaidAmount());

                return dto;
        }

        @Override
        public TransactionResponseDto payInvoice(Long invoiceId, PayInvoiceRequestDto request) throws Exception {
                Invoice invoice = invoiceRepo.findById(invoiceId).orElseThrow(PaymentNotFoundException::new);
                var transaction = transactionService.createTransaction(
                                CreateTransactionRequestDto.builder()
                                                .invoice(invoice)
                                                .customerFullName(invoice.getServiceTicket().getCustomerName())
                                                .customerPhone(invoice.getServiceTicket().getCustomerPhone())
                                                .type(PaymentTransactionType.fromValue(request.getType()))
                                                .method(TransactionMethod.fromValue(request.getMethod()))
                                                .price(request.getPrice())
                                                .build());

                if (transaction.getMethod() == TransactionMethod.BANK_TRANSFER.getValue()) {
                        return transaction;
                }

                Long customerId = invoice.getServiceTicket().getCustomer().getCustomerId();
                BigDecimal amount = new BigDecimal(transaction.getAmount());

                if (transaction.getType() == PaymentTransactionType.DEPOSIT.getValue()) {
                        invoice.setDepositReceived(invoice.getDepositReceived().add(amount));
                        invoice.setFinalAmount(invoice.getFinalAmount().subtract(amount));
                        customerService.updateTotalSpending(customerId, amount);
                } else {
                        BigDecimal finalAmount = invoice.getFinalAmount().subtract(amount);
                        invoice.setFinalAmount(finalAmount);

                        InvoiceStatus status = amount.compareTo(finalAmount) >= 0
                                        ? InvoiceStatus.PAID_IN_FULL
                                        : InvoiceStatus.UNDERPAID;

                        customerService.updateTotalSpending(customerId,
                                        amount.compareTo(finalAmount) < 0 ? amount : finalAmount);
                        invoice.setStatus(status);
                }

                invoiceRepo.save(invoice);
                return transaction;
        }

}
