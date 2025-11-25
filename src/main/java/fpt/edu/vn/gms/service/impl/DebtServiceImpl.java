package fpt.edu.vn.gms.service.impl;

import fpt.edu.vn.gms.common.enums.DebtStatus;
import fpt.edu.vn.gms.dto.CreateDebtDto;
import fpt.edu.vn.gms.dto.response.DebtResDto;
import fpt.edu.vn.gms.entity.Customer;
import fpt.edu.vn.gms.entity.Debt;
import fpt.edu.vn.gms.entity.ServiceTicket;
import fpt.edu.vn.gms.exception.CustomerNotFoundException;
import fpt.edu.vn.gms.exception.ResourceNotFoundException;
import fpt.edu.vn.gms.exception.ServiceTicketNotFoundException;
import fpt.edu.vn.gms.mapper.DebtMapper;
import fpt.edu.vn.gms.repository.CustomerRepository;
import fpt.edu.vn.gms.repository.DebtRepository;
import fpt.edu.vn.gms.repository.ServiceTicketRepository;
import fpt.edu.vn.gms.service.DebtService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;

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
    ServiceTicketRepository serviceTicketRepository;
    DebtMapper debtMapper;

    @Override
    @Transactional(readOnly = true)
    public Page<DebtResDto> getDebtsByCustomer(Long customerId, DebtStatus status, String keyword, int page, int size,
            String sort) {
        log.info("Fetching debts for customerId={} with status={} keyword={} page={} size={} sort={}",
                customerId, status, keyword, page, size, sort);

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khách hàng"));

        Pageable pageable = buildPageable(page, size, sort);
        String normalizedKeyword = StringUtils.hasText(keyword) ? keyword.trim() : null;

        Page<Debt> debts = debtRepository.findByCustomerAndFilter(customer.getCustomerId(), status, normalizedKeyword,
                pageable);
        return debts.map(debtMapper::toDto);
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
    public DebtResDto createDebt(CreateDebtDto createDebtDto) {
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
}