package fpt.edu.vn.gms.service.impl;

import fpt.edu.vn.gms.common.enums.DebtStatus;
import fpt.edu.vn.gms.common.enums.PaymentTransactionType;
import fpt.edu.vn.gms.dto.CreateDebtDto;
import fpt.edu.vn.gms.dto.CustomerDebtSummaryDto;
import fpt.edu.vn.gms.dto.PayDebtRequestDto;
import fpt.edu.vn.gms.common.enums.TransactionMethod;
import fpt.edu.vn.gms.dto.TransactionResponseDto;
import fpt.edu.vn.gms.dto.request.CreateTransactionRequestDto;
import fpt.edu.vn.gms.dto.response.CustomerDebtResponseDto;
import fpt.edu.vn.gms.dto.response.DebtDetailResponseDto;
import fpt.edu.vn.gms.dto.response.ServiceTicketDebtDetail;
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
import fpt.edu.vn.gms.mapper.ServiceTicketDebtDetailMapper;
import fpt.edu.vn.gms.repository.CustomerRepository;
import fpt.edu.vn.gms.repository.DebtRepository;
import fpt.edu.vn.gms.repository.ServiceTicketRepository;
import fpt.edu.vn.gms.repository.TransactionRepository;
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
        DebtMapper debtMapper;
        CustomerDebtMapper customerDebtMapper;
        ServiceTicketDebtDetailMapper serviceTicketDebtDetailMapper;
        TransactionService transactionService;

        @Override
        public Page<CustomerDebtSummaryDto> getAllDebtsSummary(int page, int size) {
                Pageable pageable = buildPageable(page, size, null);
                Page<CustomerDebtSummaryDto> debts = debtRepository.findTotalDebtGroupedByCustomer(pageable);
                return debts;
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
                        pageable
                );

                // Map sang DTO từng debt
                List<CustomerDebtResponseDto> debtList = customerDebtMapper.toDto(debts.getContent());

                // Tính tổng "còn lại"
                BigDecimal totalRemaining = debts.getContent().stream()
                        .map(Debt::getAmount)
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
        public TransactionResponseDto payDebt(Long debtId, PayDebtRequestDto request) throws Exception {
                Debt debt = debtRepository.findById(debtId).orElseThrow(DebtNotFoundException::new);
                var transaction = transactionService.createTransaction(
                                CreateTransactionRequestDto.builder()
                                                .debt(debt)
                                                .customerFullName(debt.getCustomer().getFullName())
                                                .customerPhone(debt.getCustomer().getPhone())
                                                .type(PaymentTransactionType.PAYMENT)
                                                .method(TransactionMethod.fromValue(request.getMethod()))
                                                .price(request.getPrice())
                                                .build());

                if (transaction.getMethod() == TransactionMethod.BANK_TRANSFER.getValue()) {
                        return transaction;
                }

                Long customerId = debt.getCustomer().getCustomerId();
                BigDecimal amount = new BigDecimal(transaction.getAmount());
                BigDecimal paidAmountAfter = debt.getPaidAmount().add(amount);
                boolean isPaidAmountAfterGreaterThanOrEqualToDebtAmount = paidAmountAfter
                                .compareTo(debt.getAmount()) >= 0;

                DebtStatus status = isPaidAmountAfterGreaterThanOrEqualToDebtAmount
                                ? DebtStatus.PAID_IN_FULL
                                : DebtStatus.OUTSTANDING;

                debt.setPaidAmount(
                                isPaidAmountAfterGreaterThanOrEqualToDebtAmount ? debt.getAmount() : paidAmountAfter);
                debt.setStatus(status);

                customerService.updateTotalSpending(customerId, paidAmountAfter
                                .compareTo(debt.getAmount()) > 0
                                                ? paidAmountAfter.subtract(
                                                                debt.getAmount())
                                                : amount);
                debtRepository.save(debt);
                return transaction;

        }


        @Override
        public ServiceTicketDebtDetail getDebtDetailByServiceTicketId(Long serviceTicketId) {

                // 1. Lấy phiếu dịch vụ
                ServiceTicket serviceTicket = serviceTicketRepository.findById(serviceTicketId)
                        .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phiếu dịch vụ"));

                String customerPhone = serviceTicket.getCustomer().getPhone();

                // 2. Lấy lịch sử theo SỐ ĐIỆN THOẠI
                List<Transaction> transactions =
                        transactionRepository.findAllByCustomerPhone(customerPhone);

                // 3. Map về DTO detail
                return serviceTicketDebtDetailMapper.toDebtDetail(serviceTicket, transactions);
        }
}