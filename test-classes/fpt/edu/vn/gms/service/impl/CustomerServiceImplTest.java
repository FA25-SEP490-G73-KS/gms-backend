package fpt.edu.vn.gms.service.impl;

import fpt.edu.vn.gms.dto.CustomerDto;
import fpt.edu.vn.gms.dto.request.CustomerRequestDto;
import fpt.edu.vn.gms.dto.VehicleInfoDto;
import fpt.edu.vn.gms.dto.response.*;
import fpt.edu.vn.gms.entity.Customer;
import fpt.edu.vn.gms.entity.DiscountPolicy;
import fpt.edu.vn.gms.entity.Employee;
import fpt.edu.vn.gms.entity.ServiceTicket;
import fpt.edu.vn.gms.entity.Vehicle;
import fpt.edu.vn.gms.entity.VehicleModel;
import fpt.edu.vn.gms.exception.ResourceNotFoundException;
import fpt.edu.vn.gms.mapper.CustomerMapper;
import fpt.edu.vn.gms.repository.CustomerRepository;
import fpt.edu.vn.gms.repository.DiscountPolicyRepository;
import fpt.edu.vn.gms.repository.ServiceTicketRepository;
import fpt.edu.vn.gms.repository.VehicleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceImplTest {

    @Mock
    CustomerRepository customerRepository;
    @Mock
    DiscountPolicyRepository discountPolicyRepository;
    @Mock
    VehicleRepository vehicleRepository;
    @Mock
    CustomerMapper customerMapper;
    @Mock
    ServiceTicketRepository serviceTicketRepository;

    @InjectMocks
    CustomerServiceImpl service;

    private Customer customer;

    @BeforeEach
    void setUp() {
        customer = Customer.builder()
                .customerId(1L)
                .fullName("Nguyen Van A")
                .phone("0909000000")
                .address("HN")
                .totalSpending(BigDecimal.ZERO)
                .build();
    }

    @Test
    void searchCustomersByPhone_ShouldReturnEmpty_WhenQueryNullOrEmpty() {
        assertTrue(service.searchCustomersByPhone(null).isEmpty());
        assertTrue(service.searchCustomersByPhone("").isEmpty());
        verifyNoInteractions(customerRepository);
    }

    @Test
    void searchCustomersByPhone_ShouldMapEntitiesToDto() {
        when(customerRepository.findTop10ByPhoneContainingOrderByPhoneAsc("0909"))
                .thenReturn(List.of(customer));

        List<CustomerDto> result = service.searchCustomersByPhone("0909");

        assertEquals(1, result.size());
        CustomerDto dto = result.get(0);
        assertEquals(customer.getCustomerId(), dto.id());
        assertEquals(customer.getFullName(), dto.name());
        assertEquals(customer.getPhone(), dto.phoneNumber());
        verify(customerRepository).findTop10ByPhoneContainingOrderByPhoneAsc("0909");
    }

    @Test
    void getCustomerDetailById_ShouldReturnDto_WhenFound() {
        CustomerDetailResponseDto dto = CustomerDetailResponseDto.builder().build();
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(customerMapper.toDetailDto(customer)).thenReturn(dto);

        CustomerDetailResponseDto result = service.getCustomerDetailById(1L);

        assertSame(dto, result);
        verify(customerRepository).findById(1L);
        verify(customerMapper).toDetailDto(customer);
    }

    @Test
    void getCustomerDetailById_ShouldThrow_WhenNotFound() {
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                () -> service.getCustomerDetailById(1L));
        verify(customerRepository).findById(1L);
    }

    @Test
    void getAllCustomers_ShouldUseRepositoryPaging() {
        Pageable pageable = Pageable.ofSize(10).withPage(0);
        Page<Customer> page = new PageImpl<>(List.of(customer), pageable, 1);
        CustomerResponseDto dto = new CustomerResponseDto();

        when(customerRepository.findAll(pageable)).thenReturn(page);
        when(customerMapper.toDto(customer)).thenReturn(dto);

        Page<CustomerResponseDto> result = service.getAllCustomers(0, 10);

        assertEquals(1, result.getTotalElements());
        assertSame(dto, result.getContent().get(0));
        verify(customerRepository).findAll(pageable);
    }

    @Test
    void getByPhone_ShouldReturnDetail_WhenFound() {
        CustomerDetailResponseDto dto = CustomerDetailResponseDto.builder().build();
        when(customerRepository.findByPhone("0909000000")).thenReturn(Optional.of(customer));
        when(customerMapper.toDetailDto(customer)).thenReturn(dto);

        CustomerDetailResponseDto result = service.getByPhone("0909000000");

        assertSame(dto, result);
        verify(customerRepository).findByPhone("0909000000");
    }

    @Test
    void getByPhone_ShouldThrow_WhenNotFound() {
        when(customerRepository.findByPhone("not-exist")).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                () -> service.getByPhone("not-exist"));
        verify(customerRepository).findByPhone("not-exist");
    }

    @Test
    void createCustomer_ShouldDeactivateOldCustomer_WhenPhoneExists() {
        CustomerRequestDto req = CustomerRequestDto.builder()
                .fullName("New Name")
                .phone("0909000000")
                .address("HN")
                .build();

        Customer oldCustomer = Customer.builder()
                .customerId(2L)
                .fullName("Old")
                .phone("0909000000")
                .isActive(true)
                .build();

        when(customerRepository.existsByPhone(anyString())).thenReturn(true);
        when(customerRepository.findByPhone(anyString())).thenReturn(Optional.of(oldCustomer));

        Customer toSave = Customer.builder()
                .customerId(3L)
                .fullName("New Name")
                .phone("0909000000")
                .address("HN")
                .build();
        when(customerMapper.toEntity(req)).thenReturn(toSave);
        when(customerRepository.save(toSave)).thenReturn(toSave);
        CustomerResponseDto dto = new CustomerResponseDto();
        when(customerMapper.toDto(toSave)).thenReturn(dto);

        CustomerResponseDto result = service.createCustomer(req);

        assertSame(dto, result);
        assertFalse(oldCustomer.getIsActive());
        verify(customerRepository).existsByPhone(anyString());
        verify(customerRepository).findByPhone(anyString());
        verify(customerRepository, times(2)).save(any(Customer.class));
    }

    @Test
    void createCustomer_ShouldCreateDirectly_WhenPhoneNotExists() {
        CustomerRequestDto req = CustomerRequestDto.builder()
                .fullName("Name")
                .phone("0909000001")
                .address("HN")
                .build();

        when(customerRepository.existsByPhone(anyString())).thenReturn(false);

        Customer entity = Customer.builder()
                .customerId(5L)
                .fullName("Name")
                .phone("0909000001")
                .build();

        when(customerMapper.toEntity(req)).thenReturn(entity);
        when(customerRepository.save(entity)).thenReturn(entity);
        CustomerResponseDto dto = new CustomerResponseDto();
        when(customerMapper.toDto(entity)).thenReturn(dto);

        CustomerResponseDto result = service.createCustomer(req);

        assertSame(dto, result);
        verify(customerRepository).existsByPhone(anyString());
        verify(customerRepository).save(entity);
    }

    @Test
    void updateCustomer_ShouldUpdateFieldsAndReturnDto_WhenPhoneNotDuplicated() {
        CustomerRequestDto req = CustomerRequestDto.builder()
                .fullName("Updated")
                .phone("0909555555")
                .address("New")
                .discountPolicyId(10L)
                .build();

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(customerRepository.existsByPhone("0909555555")).thenReturn(false);

        DiscountPolicy policy = DiscountPolicy.builder()
                .discountPolicyId(10L)
                .build();
        when(discountPolicyRepository.findById(10L)).thenReturn(Optional.of(policy));

        when(customerRepository.save(customer)).thenReturn(customer);
        CustomerResponseDto dto = new CustomerResponseDto();
        when(customerMapper.toDto(customer)).thenReturn(dto);

        CustomerResponseDto result = service.updateCustomer(1L, req);

        assertSame(dto, result);
        assertEquals("Updated", customer.getFullName());
        assertEquals("0909555555", customer.getPhone());
        assertEquals(policy, customer.getDiscountPolicy());

        verify(customerRepository).findById(1L);
        verify(discountPolicyRepository).findById(10L);
        verify(customerRepository).save(customer);
    }

    @Test
    void updateCustomer_ShouldThrow_WhenCustomerNotFound() {
        CustomerRequestDto req = CustomerRequestDto.builder()
                .fullName("Updated")
                .phone("0909111111")
                .discountPolicyId(10L)
                .build();

        when(customerRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> service.updateCustomer(1L, req));
        verify(customerRepository).findById(1L);
    }

    @Test
    void updateCustomer_ShouldThrow_WhenPhoneDuplicated() {
        CustomerRequestDto req = CustomerRequestDto.builder()
                .fullName("Updated")
                .phone("0909000001")
                .discountPolicyId(10L)
                .build();

        customer.setPhone("0909000000");

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(customerRepository.existsByPhone("0909000001")).thenReturn(true);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.updateCustomer(1L, req));
        assertTrue(ex.getMessage().contains("Số điện thoại đã tồn tại"));
    }

    @Test
    void updateCustomer_ShouldThrow_WhenDiscountPolicyNotFound() {
        CustomerRequestDto req = CustomerRequestDto.builder()
                .fullName("Updated")
                .phone("0909555555")
                .discountPolicyId(99L)
                .build();

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(customerRepository.existsByPhone("0909555555")).thenReturn(false);
        when(discountPolicyRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> service.updateCustomer(1L, req));
        verify(discountPolicyRepository).findById(99L);
    }

    @Test
    void getCustomerServiceHistoryByPhone_ShouldBuildHistory_WhenCustomerAndTicketsExist() {
        customer.setCustomerId(10L);

        when(customerRepository.findByPhone("0909000000")).thenReturn(Optional.of(customer));

        CustomerDetailDto detailDto = CustomerDetailDto.builder()
                .customerId(10L)
                .fullName(customer.getFullName())
                .phone(customer.getPhone())
                .build();
        when(customerRepository.getCustomerDetail(10L)).thenReturn(detailDto);

        VehicleModel model = VehicleModel.builder().name("Model X").brand(
                fpt.edu.vn.gms.entity.Brand.builder().name("Brand A").build()
        ).build();
        VehicleInfoDto vehicleInfo = VehicleInfoDto.builder()
                .vehicleId(100L)
                .licensePlate("30A-123.45")
                .modelName("Model X")
                .build();
        when(vehicleRepository.getCustomerVehicles(10L)).thenReturn(List.of(vehicleInfo));

        CustomerServiceHistoryDto history = CustomerServiceHistoryDto.builder()
                .serviceTicketId(1L)
                .serviceTicketCode("ST-001")
                .licensePlate("30A-123.45")
                .build();
        when(serviceTicketRepository.getCustomerServiceHistory(10L)).thenReturn(List.of(history));

        CustomerDetailDto result = service.getCustomerServiceHistoryByPhone("0909000000");

        assertEquals(customer.getFullName(), result.getFullName());
        assertEquals(1, result.getVehicles().size());
        VehicleInfoDto info = result.getVehicles().get(0);
        assertEquals(vehicleInfo.getVehicleId(), info.getVehicleId());
        assertEquals(vehicleInfo.getLicensePlate(), info.getLicensePlate());
        assertEquals(vehicleInfo.getModelName(), info.getModelName());

        verify(customerRepository).findByPhone("0909000000");
        verify(customerRepository).getCustomerDetail(10L);
        verify(vehicleRepository).getCustomerVehicles(10L);
        verify(serviceTicketRepository).getCustomerServiceHistory(10L);
    }

    @Test
    void getCustomerServiceHistoryByPhone_ShouldThrow_WhenCustomerNotFound() {
        when(customerRepository.findByPhone("x")).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                () -> service.getCustomerServiceHistoryByPhone("x"));
    }

    @Test
    void updateTotalSpending_ShouldUpdateSpendingAndBestPolicy() {
        DiscountPolicy p1 = DiscountPolicy.builder()
                .discountPolicyId(1L)
                .requiredSpending(new BigDecimal("1000000"))
                .build();
        DiscountPolicy p2 = DiscountPolicy.builder()
                .discountPolicyId(2L)
                .requiredSpending(new BigDecimal("2000000"))
                .build();

        customer.setTotalSpending(new BigDecimal("1500000"));

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(discountPolicyRepository.findAll()).thenReturn(List.of(p1, p2));

        service.updateTotalSpending(1L, new BigDecimal("1000000"));

        assertEquals(new BigDecimal("2500000"), customer.getTotalSpending());
        assertEquals(p2, customer.getDiscountPolicy());
        verify(customerRepository).save(customer);
    }

    @Test
    void updateTotalSpending_ShouldThrow_WhenCustomerNotFound() {
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                () -> service.updateTotalSpending(1L, BigDecimal.TEN));
    }

    @Test
    void getCustomers_ShouldDelegateToRepository() {
        Pageable pageable = PageRequest.of(0, 5);
        CustomerDetailDto dto = CustomerDetailDto.builder()
                .customerId(1L)
                .fullName("Test Customer")
                .build();
        Page<CustomerDetailDto> page = new PageImpl<>(
                Collections.singletonList(dto)
        );
        when(customerRepository.getAllCustomers(pageable)).thenReturn(page);

        Page<CustomerDetailDto> result = service.getCustomers(0, 5);

        assertSame(page, result);
        verify(customerRepository).getAllCustomers(pageable);
    }

    @Test
    void getCustomerDetail_ShouldPopulateVehiclesAndNullHistory() {
        CustomerDetailDto dto = new CustomerDetailDto();
        when(customerRepository.getCustomerDetail(1L)).thenReturn(dto);
        List<VehicleInfoDto> vehicles = List.of(
                VehicleInfoDto.builder().vehicleId(1L).licensePlate("30A-123.45").build()
        );
        when(vehicleRepository.getCustomerVehicles(1L)).thenReturn(vehicles);

        CustomerDetailDto result = service.getCustomerDetail(1L);

        assertSame(dto, result);
        assertEquals(vehicles, result.getVehicles());
        assertNull(result.getHistory());

        verify(customerRepository).getCustomerDetail(1L);
        verify(vehicleRepository).getCustomerVehicles(1L);
    }

    @Test
    void getServiceHistory_ShouldPopulateHistoryAndNullVehicles() {
        CustomerDetailDto dto = new CustomerDetailDto();
        when(customerRepository.getCustomerDetail(1L)).thenReturn(dto);

        List<CustomerServiceHistoryDto> history = List.of(
                CustomerServiceHistoryDto.builder()
                        .serviceTicketId(1L)
                        .serviceTicketCode("ST001")
                        .licensePlate("30A-123.45")
                        .totalAmount(BigDecimal.TEN)
                        .status(fpt.edu.vn.gms.common.enums.ServiceTicketStatus.COMPLETED)
                        .build()
        );
        when(serviceTicketRepository.getCustomerServiceHistory(1L)).thenReturn(history);

        CustomerDetailDto result = service.getServiceHistory(1L);

        assertSame(dto, result);
        assertEquals(history, result.getHistory());
        assertNull(result.getVehicles());

        verify(customerRepository).getCustomerDetail(1L);
        verify(serviceTicketRepository).getCustomerServiceHistory(1L);
    }
}


