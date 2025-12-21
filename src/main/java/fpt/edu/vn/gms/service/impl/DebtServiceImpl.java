package fpt.edu.vn.gms.service.impl;

import fpt.edu.vn.gms.common.enums.DebtStatus;
import fpt.edu.vn.gms.common.enums.PaymentTransactionType;
import fpt.edu.vn.gms.dto.request.CreateDebtDto;
import fpt.edu.vn.gms.dto.response.*;
import fpt.edu.vn.gms.dto.request.PayDebtRequestDto;
import fpt.edu.vn.gms.common.enums.TransactionMethod;
import fpt.edu.vn.gms.dto.request.CreateTransactionRequestDto;
import fpt.edu.vn.gms.entity.Customer;
import fpt.edu.vn.gms.entity.Debt;
import fpt.edu.vn.gms.entity.ServiceTicket;
import fpt.edu.vn.gms.entity.Transaction;
import fpt.edu.vn.gms.exception.CustomerNotFoundException;
import fpt.edu.vn.gms.exception.DebtNotFoundException;
import fpt.edu.vn.gms.exception.ResourceNotFoundException;
import fpt.edu.vn.gms.exception.ServiceTicketNotFoundException;
import fpt.edu.vn.gms.mapper.CustomerDebtMapper;
import fpt.edu.vn.gms.mapper.DebtMapper;
import fpt.edu.vn.gms.mapper.InvoiceMapper;
import fpt.edu.vn.gms.mapper.ServiceTicketDebtDetailMapper;
import fpt.edu.vn.gms.repository.*;
import fpt.edu.vn.gms.service.CustomerService;
import fpt.edu.vn.gms.service.DebtService;
import fpt.edu.vn.gms.service.TransactionService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class DebtServiceImpl implements DebtService {

        private static final int NUMBER_OF_DEBT_DAYS = 14;

        DebtRepository debtRepository;
        CustomerRepository customerRepository;
        TransactionRepository transactionRepository;
        CustomerService customerService;
        ServiceTicketRepository serviceTicketRepository;
        InvoiceRepository invoiceRepository;
        InvoiceMapper invoiceMapper;
        DebtMapper debtMapper;
        CustomerDebtMapper customerDebtMapper;
        ServiceTicketDebtDetailMapper serviceTicketDebtDetailMapper;
        TransactionService transactionService;

        @Override
        public Page<CustomerDebtSummaryDto> getAllDebtsSummary(int page, int size) {
                // For summary aggregation query, do NOT sort by createdAt (not in GROUP BY) to
                // avoid ONLY_FULL_GROUP_BY error
                Pageable pageable = PageRequest.of(page, size);
                Page<Object[]> rawPage = debtRepository.findTotalDebtGroupedByCustomer(null, null, null, pageable);

                List<CustomerDebtSummaryDto> content = rawPage.getContent().stream()
                                .map(this::mapToCustomerDebtSummaryDto)
                                .toList();

                return new PageImpl<>(content, pageable, rawPage.getTotalElements());
        }

        @Override
        public Page<CustomerDebtSummaryDto> getAllDebtsSummary(int page, int size, DebtStatus status,
                        LocalDate fromDate,
                        LocalDate toDate) {
                Pageable pageable = PageRequest.of(page, size);
                Page<Object[]> rawPage = debtRepository.findTotalDebtGroupedByCustomer(status, fromDate, toDate,
                                pageable);

                List<CustomerDebtSummaryDto> content = rawPage.getContent().stream()
                                .map(this::mapToCustomerDebtSummaryDto)
                                .toList();

                return new PageImpl<>(content, pageable, rawPage.getTotalElements());
        }

        private CustomerDebtSummaryDto mapToCustomerDebtSummaryDto(Object[] row) {
                Long customerId = row[0] != null ? ((Number) row[0]).longValue() : null;
                String fullName = (String) row[1];
                String phone = (String) row[2];

                BigDecimal totalAmount = row[3] != null ? (BigDecimal) row[3] : BigDecimal.ZERO;
                BigDecimal totalPaidAmount = row[4] != null ? (BigDecimal) row[4] : BigDecimal.ZERO;
                BigDecimal totalRemaining = row[5] != null ? (BigDecimal) row[5] : BigDecimal.ZERO;

                String status = (String) row[6];

                return new CustomerDebtSummaryDto(
                                customerId,
                                fullName,
                                phone,
                                totalAmount,
                                totalPaidAmount,
                                totalRemaining,
                                status);
        }

        @Override
        @Transactional(readOnly = true)
        public DebtDetailResponseDto getDebtsByCustomer(Long customerId,
                        DebtStatus status,
                        String keyword,
                        int page,
                        int size,
                        String sort) {

                log.info("Fetching debts for customerId={} status={} keyword={} page={} size={} sort={}",
                                customerId, status, keyword, page, size, sort);

                // Lấy customer
                Customer customer = customerRepository.findById(customerId)
                                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khách hàng"));

                // Build pageable
                Pageable pageable = buildPageable(page, size, sort);
                String normalizedKeyword = StringUtils.hasText(keyword) ? keyword.trim() : null;

                // Lấy danh sách công nợ của KH
                Page<Debt> debts = debtRepository.findByCustomerAndFilter(
                                customer.getCustomerId(),
                                status,
                                normalizedKeyword,
                                pageable);

                // Map sang DTO từng debt
                List<CustomerDebtResponseDto> debtList = customerDebtMapper.toDto(debts.getContent());

                // Tính tổng "còn lại"
                BigDecimal totalRemaining = debts.getContent().stream()
                                .map(debt -> {
                                        BigDecimal amount = debt.getAmount() != null ? debt.getAmount()
                                                        : BigDecimal.ZERO;
                                        BigDecimal paidAmount = debt.getPaidAmount() != null ? debt.getPaidAmount()
                                                        : BigDecimal.ZERO;
                                        return amount.subtract(paidAmount);
                                })
                                .reduce(BigDecimal.ZERO, BigDecimal::add);

                // Lấy biển số đầu tiên
                String licensePlate = customer.getVehicles().isEmpty()
                                ? null
                                : customer.getVehicles().get(0).getLicensePlate();

                // Build response cho FE
                return DebtDetailResponseDto.builder()
                                .customerName(customer.getFullName())
                                .phone(customer.getPhone())
                                .licensePlate(licensePlate)
                                .address(customer.getAddress())
                                .debts(debtList)
                                .totalRemainingAmount(totalRemaining)
                                .build();
        }

        private Pageable buildPageable(int page, int size, String sort) {
                if (!StringUtils.hasText(sort)) {
                        return PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
                }

                String[] sortParams = sort.split(",");
                Sort.Direction direction = Sort.Direction.DESC;
                String property = "createdAt";

                if (sortParams.length > 0 && StringUtils.hasText(sortParams[0])) {
                        property = sortParams[0];
                }

                if (sortParams.length > 1 && StringUtils.hasText(sortParams[1])) {
                        direction = Sort.Direction.fromString(sortParams[1]);
                }

                return PageRequest.of(page, size, Sort.by(direction, property));
        }

        @Override
        public DebtDetailResponseDto createDebt(CreateDebtDto createDebtDto) {
                Customer customer = customerRepository.findById(createDebtDto.getCustomerId())
                                .orElseThrow(CustomerNotFoundException::new);
                ServiceTicket serviceTicket = serviceTicketRepository.findById(createDebtDto.getServiceTicketId())
                                .orElseThrow(ServiceTicketNotFoundException::new);

                return debtMapper.toDto(
                                debtRepository.save(
                                                Debt.builder()
                                                                .customer(customer)
                                                                .serviceTicket(serviceTicket)
                                                                .amount(createDebtDto.getAmount())
                                                                .dueDate(LocalDate.now().plusDays(NUMBER_OF_DEBT_DAYS))
                                                                .build()));
        }

        @Override
        @Transactional
        public TransactionResponseDto payDebt(Long debtId, PayDebtRequestDto request) throws Exception {
                log.info("Bắt đầu thanh toán công nợ: debtId={}, method={}, price={}",
                                debtId, request.getMethod(), request.getPrice());

                Debt debt = debtRepository.findById(debtId).orElseThrow(DebtNotFoundException::new);
                log.info("Tìm thấy debt: id={}, amount={}, paidAmount={}",
                                debt.getId(), debt.getAmount(), debt.getPaidAmount());

                var transaction = transactionService.createTransaction(
                                CreateTransactionRequestDto.builder()
                                                .debt(debt)
                                                .customerFullName(debt.getCustomer().getFullName())
                                                .customerPhone(debt.getCustomer().getPhone())
                                                .type(PaymentTransactionType.PAYMENT)
                                                .method(TransactionMethod.fromValue(request.getMethod()))
                                                .price(request.getPrice())
                                                .build());

                log.info("Đã tạo transaction: id={}, method={}, amount={}",
                                transaction.getId(), transaction.getMethod(), transaction.getAmount());

                // Chỉ return sớm nếu là BANK_TRANSFER (phải đợi callback)
                if (TransactionMethod.BANK_TRANSFER.getValue().equals(transaction.getMethod())) {
                        log.info("Thanh toán BANK_TRANSFER, return sớm và đợi callback");
                        return transaction;
                }

                // Xử lý thanh toán CASH: cập nhật paidAmount ngay
                Long customerId = debt.getCustomer().getCustomerId();

                // Kiểm tra null và lấy giá trị an toàn
                Long transactionAmount = transaction.getAmount();
                if (transactionAmount == null) {
                        log.error("Transaction amount is null! transactionId={}", transaction.getId());
                        throw new IllegalStateException("Số tiền giao dịch không hợp lệ");
                }

                BigDecimal amount = BigDecimal.valueOf(transactionAmount);
                BigDecimal currentPaidAmount = debt.getPaidAmount() != null
                                ? debt.getPaidAmount()
                                : BigDecimal.ZERO;
                BigDecimal paidAmountAfter = currentPaidAmount.add(amount);
                boolean isPaidAmountAfterGreaterThanOrEqualToDebtAmount = paidAmountAfter
                                .compareTo(debt.getAmount()) >= 0;

                DebtStatus status = isPaidAmountAfterGreaterThanOrEqualToDebtAmount
                                ? DebtStatus.PAID_IN_FULL
                                : DebtStatus.OUTSTANDING;

                log.info("Cập nhật paidAmount: current={}, amount={}, after={}, status={}",
                                currentPaidAmount, amount, paidAmountAfter, status);

                debt.setPaidAmount(
                                isPaidAmountAfterGreaterThanOrEqualToDebtAmount ? debt.getAmount() : paidAmountAfter);
                debt.setStatus(status);

                customerService.updateTotalSpending(customerId, paidAmountAfter
                                .compareTo(debt.getAmount()) > 0
                                                ? paidAmountAfter.subtract(
                                                                debt.getAmount())
                                                : amount);

                Debt savedDebt = debtRepository.save(debt);
                log.info("Đã lưu debt: id={}, paidAmount={}, status={}",
                                savedDebt.getId(), savedDebt.getPaidAmount(), savedDebt.getStatus());

                return transaction;

        }

        @Override
        public void updateDueDate(Long debtId, LocalDate dueDate) {
                // Validate dueDate: must be strictly after today
                LocalDate today = LocalDate.now();
                if (dueDate == null || !dueDate.isAfter(today)) {
                        throw new IllegalArgumentException("Ngày đến hạn phải sau ngày hiện tại");
                }

                Debt debt = debtRepository.findById(debtId)
                                .orElseThrow(DebtNotFoundException::new);

                debt.setDueDate(dueDate);
                debtRepository.save(debt);
        }

        @Override
        public ServiceTicketDebtDetail getDebtDetailByServiceTicketId(Long serviceTicketId) {

                // 1. Lấy phiếu dịch vụ
                ServiceTicket serviceTicket = serviceTicketRepository.findById(serviceTicketId)
                                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phiếu dịch vụ"));

                String customerPhone = serviceTicket.getCustomer().getPhone();

                // 2. Lấy debt theo ServiceTicket (nếu có)
                Debt debt = debtRepository.findByServiceTicket_ServiceTicketId(serviceTicketId)
                                .orElse(null);

                Long debtId = debt != null ? debt.getId() : null;

                // 3. Lấy lịch sử theo SỐ ĐIỆN THOẠI và debtId tương ứng (chỉ lấy giao dịch đã
                // active)
                List<Transaction> transactions = (debtId == null)
                                ? List.of()
                                : transactionRepository.findAllByCustomerPhoneAndDebt_IdAndIsActiveTrue(customerPhone,
                                                debtId);

                // 4. Map về DTO detail (transactions)
                ServiceTicketDebtDetail detail = serviceTicketDebtDetailMapper.toDebtDetail(serviceTicket,
                                transactions);

                // 6. Gắn thêm thông tin chi tiết công nợ nếu có
                if (debt != null) {
                        CustomerDebtResponseDto customerDebtDto = customerDebtMapper.toDto(debt);
                        detail.setCustomerDebt(customerDebtDto);
                }

                // 7. Gắn thêm thông tin invoice nếu có
                invoiceRepository.findByServiceTicket_ServiceTicketId(serviceTicketId)
                                .ifPresent(invoice -> {
                                        InvoiceDetailResDto invoiceDto = invoiceMapper.toDetailDto(invoice);
                                        detail.setInvoice(invoiceDto);
                                });

                return detail;
        }
}