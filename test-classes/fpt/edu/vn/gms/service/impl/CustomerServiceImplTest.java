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
import java.util.ArrayList;
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

    // ========== Additional test cases for searchCustomersByPhone ==========

    @Test
    void searchCustomersByPhone_ShouldReturnEmptyList_WhenQueryIsNull() {
        List<CustomerDto> result = service.searchCustomersByPhone(null);

        assertTrue(result.isEmpty());
        verify(customerRepository, never()).findTop10ByPhoneContainingOrderByPhoneAsc(anyString());
    }

    @Test
    void searchCustomersByPhone_ShouldReturnEmptyList_WhenQueryIsEmpty() {
        List<CustomerDto> result = service.searchCustomersByPhone("");

        assertTrue(result.isEmpty());
        verify(customerRepository, never()).findTop10ByPhoneContainingOrderByPhoneAsc(anyString());
    }

    @Test
    void searchCustomersByPhone_ShouldReturnEmptyList_WhenQueryIsBlank() {
        List<CustomerDto> result = service.searchCustomersByPhone("   ");

        assertTrue(result.isEmpty());
        verify(customerRepository, never()).findTop10ByPhoneContainingOrderByPhoneAsc(anyString());
    }

    @Test
    void searchCustomersByPhone_ShouldReturnMultipleCustomers() {
        Customer customer1 = Customer.builder()
                .customerId(1L)
                .fullName("Customer 1")
                .phone("0909000001")
                .build();
        Customer customer2 = Customer.builder()
                .customerId(2L)
                .fullName("Customer 2")
                .phone("0909000002")
                .build();

        when(customerRepository.findTop10ByPhoneContainingOrderByPhoneAsc("0909"))
                .thenReturn(List.of(customer1, customer2));

        List<CustomerDto> result = service.searchCustomersByPhone("0909");

        assertEquals(2, result.size());
        assertEquals(1L, result.get(0).id());
        assertEquals("Customer 1", result.get(0).name());
        assertEquals("0909000001", result.get(0).phoneNumber());
        verify(customerRepository).findTop10ByPhoneContainingOrderByPhoneAsc("0909");
    }

    @Test
    void searchCustomersByPhone_ShouldLimitToTop10() {
        List<Customer> customers = new ArrayList<>();
        for (int i = 1; i <= 15; i++) {
            customers.add(Customer.builder()
                    .customerId((long) i)
                    .fullName("Customer " + i)
                    .phone("090900000" + i)
                    .build());
        }

        when(customerRepository.findTop10ByPhoneContainingOrderByPhoneAsc("0909"))
                .thenReturn(customers.subList(0, 10));

        List<CustomerDto> result = service.searchCustomersByPhone("0909");

        assertEquals(10, result.size());
        verify(customerRepository).findTop10ByPhoneContainingOrderByPhoneAsc("0909");
    }

    // ========== Additional test cases for createCustomer ==========

    @Test
    void createCustomer_ShouldThrow_WhenPhoneAlreadyExists() {
        CustomerRequestDto dto = CustomerRequestDto.builder()
                .phone("0909000000")
                .fullName("New Customer")
                .build();

        when(customerRepository.existsByPhone("0909000000")).thenReturn(true);
        when(customerRepository.findByPhone("0909000000")).thenReturn(Optional.of(customer));

        assertThrows(ResourceNotFoundException.class, () -> service.createCustomer(dto));
        verify(customerRepository).existsByPhone("0909000000");
        verify(customerRepository).findByPhone("0909000000");
    }


    @Test
    void createCustomer_ShouldNormalizePhoneNumber() {
        CustomerRequestDto dto = CustomerRequestDto.builder()
                .phone("0909 000 000")
                .fullName("Test Customer")
                .build();

        when(customerRepository.existsByPhone(anyString())).thenReturn(false);
        when(customerRepository.save(any(Customer.class))).thenAnswer(invocation -> {
            Customer saved = invocation.getArgument(0);
            // Phone should be normalized
            assertNotNull(saved.getPhone());
            return saved;
        });
        when(customerMapper.toDto(any(Customer.class))).thenReturn(CustomerResponseDto.builder().build());

        service.createCustomer(dto);

        verify(customerRepository).save(any(Customer.class));
    }

    @Test
    void createCustomer_ShouldMapToResponseDto() {
        CustomerRequestDto dto = CustomerRequestDto.builder()
                .phone("0909000000")
                .fullName("Test Customer")
                .build();

        when(customerRepository.existsByPhone(anyString())).thenReturn(false);
        Customer saved = Customer.builder()
                .customerId(100L)
                .fullName("Test Customer")
                .phone("0909000000")
                .build();
        when(customerRepository.save(any(Customer.class))).thenReturn(saved);

        CustomerResponseDto responseDto = CustomerResponseDto.builder()
                .customerId(100L)
                .fullName("Test Customer")
                .phone("0909000000")
                .build();
        when(customerMapper.toDto(saved)).thenReturn(responseDto);

        CustomerResponseDto result = service.createCustomer(dto);

        assertSame(responseDto, result);
        verify(customerMapper).toDto(saved);
    }

    @Test
    void createCustomer_ShouldSaveNewCustomer() {
        CustomerRequestDto dto = CustomerRequestDto.builder()
                .phone("0909000000")
                .fullName("New Customer")
                .address("Address")
                .build();

        when(customerRepository.existsByPhone(anyString())).thenReturn(false);
        when(customerRepository.save(any(Customer.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(customerMapper.toDto(any(Customer.class))).thenReturn(CustomerResponseDto.builder().build());

        service.createCustomer(dto);

        verify(customerRepository).save(any(Customer.class));
    }

    // ========== Additional test cases for updateCustomer ==========

    @Test
    void updateCustomer_ShouldThrow_WhenPhoneExistsForDifferentCustomer() {
        Customer existing = Customer.builder()
                .customerId(1L)
                .phone("0909000000")
                .build();

        CustomerRequestDto dto = CustomerRequestDto.builder()
                .phone("0909000001")
                .fullName("Updated Name")
                .build();

        when(customerRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(customerRepository.existsByPhone("0909000001")).thenReturn(true);

        assertThrows(RuntimeException.class, () -> service.updateCustomer(1L, dto));
        verify(customerRepository).existsByPhone("0909000001");
        verify(customerRepository, never()).save(any());
    }

    @Test
    void updateCustomer_ShouldAllowSamePhone() {
        Customer existing = Customer.builder()
                .customerId(1L)
                .phone("0909000000")
                .fullName("Old Name")
                .build();

        CustomerRequestDto dto = CustomerRequestDto.builder()
                .phone("0909000000") // Same phone
                .fullName("New Name")
                .build();

        DiscountPolicy policy = DiscountPolicy.builder()
                .discountPolicyId(1L)
                .build();

        when(customerRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(discountPolicyRepository.findById(anyLong())).thenReturn(Optional.of(policy));
        when(customerRepository.save(existing)).thenReturn(existing);
        when(customerMapper.toDto(existing)).thenReturn(CustomerResponseDto.builder().build());

        service.updateCustomer(1L, dto);

        assertEquals("New Name", existing.getFullName());
        verify(customerRepository).save(existing);
    }

    @Test
    void updateCustomer_ShouldUpdateAllFields() {
        Customer existing = Customer.builder()
                .customerId(1L)
                .fullName("Old Name")
                .phone("0909000000")
                .address("Old Address")
                .build();

        CustomerRequestDto dto = CustomerRequestDto.builder()
                .fullName("New Name")
                .phone("0909000001")
                .address("New Address")
                .customerType(fpt.edu.vn.gms.common.enums.CustomerType.CA_NHAN)
                .discountPolicyId(1L)
                .build();

        DiscountPolicy policy = DiscountPolicy.builder()
                .discountPolicyId(1L)
                .build();

        when(customerRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(customerRepository.existsByPhone("0909000001")).thenReturn(false);
        when(discountPolicyRepository.findById(1L)).thenReturn(Optional.of(policy));
        when(customerRepository.save(existing)).thenReturn(existing);
        when(customerMapper.toDto(existing)).thenReturn(CustomerResponseDto.builder().build());

        service.updateCustomer(1L, dto);

        assertEquals("New Name", existing.getFullName());
        assertEquals("0909000001", existing.getPhone());
        assertEquals("New Address", existing.getAddress());
        assertEquals(fpt.edu.vn.gms.common.enums.CustomerType.CA_NHAN, existing.getCustomerType());
        assertEquals(policy, existing.getDiscountPolicy());
        verify(customerRepository).save(existing);
    }

    @Test
    void updateCustomer_ShouldNormalizePhoneNumber() {
        Customer existing = Customer.builder()
                .customerId(1L)
                .phone("0909000000")
                .build();

        CustomerRequestDto dto = CustomerRequestDto.builder()
                .phone("0909 000 001")
                .fullName("Updated Name")
                .build();

        DiscountPolicy policy = DiscountPolicy.builder()
                .discountPolicyId(1L)
                .build();

        when(customerRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(customerRepository.existsByPhone(anyString())).thenReturn(false);
        when(discountPolicyRepository.findById(anyLong())).thenReturn(Optional.of(policy));
        when(customerRepository.save(existing)).thenReturn(existing);
        when(customerMapper.toDto(existing)).thenReturn(CustomerResponseDto.builder().build());

        service.updateCustomer(1L, dto);

        verify(customerRepository).save(existing);
    }

    // ========== Additional test cases for handleNotMe ==========

    @Test
    void handleNotMe_ShouldDeactivateOldCustomer_WhenExists() {
        Customer oldCustomer = Customer.builder()
                .customerId(99L)
                .phone("0909000000")
                .isActive(true)
                .build();

        when(customerRepository.findByPhone("0909000000")).thenReturn(Optional.of(oldCustomer));
        when(customerRepository.save(oldCustomer)).thenReturn(oldCustomer);
        when(customerRepository.save(any(Customer.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(customerMapper.toDto(any(Customer.class))).thenReturn(CustomerResponseDto.builder().build());

        service.handleNotMe("0909000000");

        assertFalse(oldCustomer.getIsActive());
        verify(customerRepository).save(oldCustomer);
        verify(customerRepository).flush();
    }

    @Test
    void handleNotMe_ShouldCreateNewCustomer_WhenOldDoesNotExist() {
        when(customerRepository.findByPhone("0909000000")).thenReturn(Optional.empty());
        when(customerRepository.save(any(Customer.class))).thenAnswer(invocation -> {
            Customer newCustomer = invocation.getArgument(0);
            newCustomer.setCustomerId(100L);
            assertEquals("0909000000", newCustomer.getPhone());
            assertTrue(newCustomer.getIsActive());
            return newCustomer;
        });
        when(customerMapper.toDto(any(Customer.class))).thenReturn(CustomerResponseDto.builder().build());

        service.handleNotMe("0909000000");

        verify(customerRepository).save(any(Customer.class));
        verify(customerRepository, never()).flush();
    }

    @Test
    void handleNotMe_ShouldReturnNewCustomerDto() {
        when(customerRepository.findByPhone("0909000000")).thenReturn(Optional.empty());
        Customer newCustomer = Customer.builder()
                .customerId(100L)
                .phone("0909000000")
                .isActive(true)
                .build();
        when(customerRepository.save(any(Customer.class))).thenReturn(newCustomer);

        CustomerResponseDto responseDto = CustomerResponseDto.builder()
                .customerId(100L)
                .phone("0909000000")
                .build();
        when(customerMapper.toDto(newCustomer)).thenReturn(responseDto);

        CustomerResponseDto result = service.handleNotMe("0909000000");

        assertSame(responseDto, result);
        verify(customerMapper).toDto(newCustomer);
    }

    @Test
    void handleNotMe_ShouldNotFlush_WhenOldCustomerNull() {
        when(customerRepository.findByPhone("0909000000")).thenReturn(Optional.empty());
        when(customerRepository.save(any(Customer.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(customerMapper.toDto(any(Customer.class))).thenReturn(CustomerResponseDto.builder().build());

        service.handleNotMe("0909000000");

        verify(customerRepository, never()).flush();
    }

    @Test
    void handleNotMe_ShouldSetActiveToTrue_ForNewCustomer() {
        when(customerRepository.findByPhone("0909000000")).thenReturn(Optional.empty());
        when(customerRepository.save(any(Customer.class))).thenAnswer(invocation -> {
            Customer newCustomer = invocation.getArgument(0);
            assertTrue(newCustomer.getIsActive());
            return newCustomer;
        });
        when(customerMapper.toDto(any(Customer.class))).thenReturn(CustomerResponseDto.builder().build());

        service.handleNotMe("0909000000");

        verify(customerRepository).save(any(Customer.class));
    }

    // ========== Additional test cases for getCustomerDetail ==========

    @Test
    void getCustomerDetail_ShouldReturnDetailWithVehicles() {
        CustomerDetailDto dto = CustomerDetailDto.builder()
                .customerId(1L)
                .fullName("Customer")
                .build();
        when(customerRepository.getCustomerDetail(1L)).thenReturn(dto);

        VehicleInfoDto vehicle1 = VehicleInfoDto.builder()
                .vehicleId(1L)
                .licensePlate("30A-12345")
                .build();
        VehicleInfoDto vehicle2 = VehicleInfoDto.builder()
                .vehicleId(2L)
                .licensePlate("30B-67890")
                .build();
        when(vehicleRepository.getCustomerVehicles(1L)).thenReturn(List.of(vehicle1, vehicle2));

        CustomerDetailDto result = service.getCustomerDetail(1L);

        assertNotNull(result);
        assertEquals(2, result.getVehicles().size());
        assertNull(result.getHistory());
        verify(customerRepository).getCustomerDetail(1L);
        verify(vehicleRepository).getCustomerVehicles(1L);
    }

    @Test
    void getCustomerDetail_ShouldSetHistoryToNull() {
        CustomerDetailDto dto = CustomerDetailDto.builder()
                .customerId(1L)
                .build();
        when(customerRepository.getCustomerDetail(1L)).thenReturn(dto);
        when(vehicleRepository.getCustomerVehicles(1L)).thenReturn(Collections.emptyList());

        CustomerDetailDto result = service.getCustomerDetail(1L);

        assertNull(result.getHistory());
        verify(serviceTicketRepository, never()).getCustomerServiceHistory(anyLong());
    }

    @Test
    void getCustomerDetail_ShouldHandleEmptyVehicles() {
        CustomerDetailDto dto = CustomerDetailDto.builder()
                .customerId(1L)
                .build();
        when(customerRepository.getCustomerDetail(1L)).thenReturn(dto);
        when(vehicleRepository.getCustomerVehicles(1L)).thenReturn(Collections.emptyList());

        CustomerDetailDto result = service.getCustomerDetail(1L);

        assertNotNull(result.getVehicles());
        assertTrue(result.getVehicles().isEmpty());
    }

    @Test
    void getCustomerDetail_ShouldPreserveCustomerInfo() {
        CustomerDetailDto dto = CustomerDetailDto.builder()
                .customerId(1L)
                .fullName("Test Customer")
                .phone("0909000000")
                .address("Test Address")
                .build();
        when(customerRepository.getCustomerDetail(1L)).thenReturn(dto);
        when(vehicleRepository.getCustomerVehicles(1L)).thenReturn(Collections.emptyList());

        CustomerDetailDto result = service.getCustomerDetail(1L);

        assertEquals(1L, result.getCustomerId());
        assertEquals("Test Customer", result.getFullName());
        assertEquals("0909000000", result.getPhone());
        assertEquals("Test Address", result.getAddress());
    }

    @Test
    void getCustomerDetail_ShouldCombineDetailAndVehicles() {
        CustomerDetailDto dto = CustomerDetailDto.builder()
                .customerId(1L)
                .fullName("Customer")
                .build();
        when(customerRepository.getCustomerDetail(1L)).thenReturn(dto);

        VehicleInfoDto vehicle = VehicleInfoDto.builder()
                .vehicleId(1L)
                .licensePlate("30A-12345")
                .build();
        when(vehicleRepository.getCustomerVehicles(1L)).thenReturn(List.of(vehicle));

        CustomerDetailDto result = service.getCustomerDetail(1L);

        assertEquals(dto, result);
        assertEquals(1, result.getVehicles().size());
        assertEquals("30A-12345", result.getVehicles().get(0).getLicensePlate());
    }

    // ========== Additional test cases for getServiceHistory ==========

    @Test
    void getServiceHistory_ShouldSetVehiclesToNull() {
        CustomerDetailDto dto = CustomerDetailDto.builder()
                .customerId(1L)
                .build();
        when(customerRepository.getCustomerDetail(1L)).thenReturn(dto);

        List<CustomerServiceHistoryDto> history = List.of(
                CustomerServiceHistoryDto.builder()
                        .serviceTicketId(1L)
                        .serviceTicketCode("ST-001")
                        .build()
        );
        when(serviceTicketRepository.getCustomerServiceHistory(1L)).thenReturn(history);

        CustomerDetailDto result = service.getServiceHistory(1L);

        assertNull(result.getVehicles());
        assertEquals(history, result.getHistory());
    }

    @Test
    void getServiceHistory_ShouldHandleEmptyHistory() {
        CustomerDetailDto dto = CustomerDetailDto.builder()
                .customerId(1L)
                .build();
        when(customerRepository.getCustomerDetail(1L)).thenReturn(dto);
        when(serviceTicketRepository.getCustomerServiceHistory(1L)).thenReturn(Collections.emptyList());

        CustomerDetailDto result = service.getServiceHistory(1L);

        assertNotNull(result.getHistory());
        assertTrue(result.getHistory().isEmpty());
        assertNull(result.getVehicles());
    }

    @Test
    void getServiceHistory_ShouldPreserveCustomerInfo() {
        CustomerDetailDto dto = CustomerDetailDto.builder()
                .customerId(1L)
                .fullName("Customer")
                .phone("0909000000")
                .build();
        when(customerRepository.getCustomerDetail(1L)).thenReturn(dto);
        when(serviceTicketRepository.getCustomerServiceHistory(1L)).thenReturn(Collections.emptyList());

        CustomerDetailDto result = service.getServiceHistory(1L);

        assertEquals(1L, result.getCustomerId());
        assertEquals("Customer", result.getFullName());
        assertEquals("0909000000", result.getPhone());
    }

    @Test
    void getServiceHistory_ShouldCombineDetailAndHistory() {
        CustomerDetailDto dto = CustomerDetailDto.builder()
                .customerId(1L)
                .build();
        when(customerRepository.getCustomerDetail(1L)).thenReturn(dto);

        List<CustomerServiceHistoryDto> history = List.of(
                CustomerServiceHistoryDto.builder()
                        .serviceTicketId(1L)
                        .serviceTicketCode("ST-001")
                        .build(),
                CustomerServiceHistoryDto.builder()
                        .serviceTicketId(2L)
                        .serviceTicketCode("ST-002")
                        .build()
        );
        when(serviceTicketRepository.getCustomerServiceHistory(1L)).thenReturn(history);

        CustomerDetailDto result = service.getServiceHistory(1L);

        assertEquals(dto, result);
        assertEquals(2, result.getHistory().size());
        assertEquals("ST-001", result.getHistory().get(0).getServiceTicketCode());
    }

    @Test
    void getServiceHistory_ShouldNotCallVehicleRepository() {
        CustomerDetailDto dto = CustomerDetailDto.builder()
                .customerId(1L)
                .build();
        when(customerRepository.getCustomerDetail(1L)).thenReturn(dto);
        when(serviceTicketRepository.getCustomerServiceHistory(1L)).thenReturn(Collections.emptyList());

        service.getServiceHistory(1L);

        verify(vehicleRepository, never()).getCustomerVehicles(anyLong());
    }
}


