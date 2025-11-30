package fpt.edu.vn.gms.service.impl;

import fpt.edu.vn.gms.base.BaseServiceTest;
import fpt.edu.vn.gms.common.enums.CustomerType;
import fpt.edu.vn.gms.common.enums.ServiceTicketStatus;
import fpt.edu.vn.gms.dto.CustomerDto;
import fpt.edu.vn.gms.dto.VehicleInfoDto;
import fpt.edu.vn.gms.dto.request.CustomerRequestDto;
import fpt.edu.vn.gms.dto.response.*;
import fpt.edu.vn.gms.entity.Customer;
import fpt.edu.vn.gms.entity.DiscountPolicy;
import fpt.edu.vn.gms.entity.ServiceTicket;
import fpt.edu.vn.gms.entity.Vehicle;
import fpt.edu.vn.gms.entity.VehicleModel;
import fpt.edu.vn.gms.entity.Brand;
import fpt.edu.vn.gms.exception.ResourceNotFoundException;
import fpt.edu.vn.gms.mapper.CustomerMapper;
import fpt.edu.vn.gms.repository.CustomerRepository;
import fpt.edu.vn.gms.repository.DiscountPolicyRepository;
import fpt.edu.vn.gms.repository.ServiceTicketRepository;
import fpt.edu.vn.gms.repository.VehicleRepository;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CustomerServiceImplTest extends BaseServiceTest {

  @Mock
  private CustomerRepository customerRepository;
  @Mock
  private DiscountPolicyRepository discountPolicyRepository;
  @Mock
  private VehicleRepository vehicleRepository;
  @Mock
  private CustomerMapper customerMapper;
  @Mock
  private ServiceTicketRepository serviceTicketRepository;

  @InjectMocks
  private CustomerServiceImpl customerServiceImpl;

  @Test
  void searchCustomersByPhone_WhenQueryIsNull_ShouldReturnEmptyList() {
    List<CustomerDto> result = customerServiceImpl.searchCustomersByPhone(null);
    assertTrue(result.isEmpty());
  }

  @Test
  void searchCustomersByPhone_WhenQueryIsEmpty_ShouldReturnEmptyList() {
    List<CustomerDto> result = customerServiceImpl.searchCustomersByPhone("");
    assertTrue(result.isEmpty());
  }

  @Test
  void searchCustomersByPhone_WhenQueryValid_ShouldReturnCustomerDtos() {
    Customer customer = Customer.builder().customerId(1L).fullName("A").phone("0123").build();
    when(customerRepository.findTop10ByPhoneContainingOrderByPhoneAsc("0123"))
        .thenReturn(List.of(customer));
    List<CustomerDto> result = customerServiceImpl.searchCustomersByPhone("0123");
    assertEquals(1, result.size());
    assertEquals("A", result.get(0).name());
    assertEquals("0123", result.get(0).phoneNumber());
  }

  @Test
  void getCustomerDetailById_WhenCustomerExists_ShouldReturnDetailDto() {
    Customer customer = Customer.builder().customerId(1L).build();
    CustomerDetailResponseDto dto = CustomerDetailResponseDto.builder().customerId(1L).build();
    when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
    when(customerMapper.toDetailDto(customer)).thenReturn(dto);

    CustomerDetailResponseDto result = customerServiceImpl.getCustomerDetailById(1L);
    assertEquals(1L, result.getCustomerId());
  }

  @Test
  void getCustomerDetailById_WhenCustomerNotFound_ShouldThrowException() {
    when(customerRepository.findById(99L)).thenReturn(Optional.empty());
    assertThrows(ResourceNotFoundException.class, () -> customerServiceImpl.getCustomerDetailById(99L));
  }

  @Test
  void getAllCustomers_WhenCalled_ShouldReturnPagedResult() {
    Customer customer = Customer.builder().customerId(1L).build();
    CustomerResponseDto dto = CustomerResponseDto.builder().customerId(1L).build();
    Page<Customer> page = new PageImpl<>(List.of(customer));
    when(customerRepository.findAll(any(Pageable.class))).thenReturn(page);
    when(customerMapper.toDto(customer)).thenReturn(dto);

    Page<CustomerResponseDto> result = customerServiceImpl.getAllCustomers(0, 10);
    assertEquals(1, result.getTotalElements());
    assertEquals(1L, result.getContent().get(0).getCustomerId());
  }

  @Test
  void getByPhone_WhenCustomerExists_ShouldReturnDetailDto() {
    Customer customer = Customer.builder().customerId(1L).build();
    CustomerDetailResponseDto dto = CustomerDetailResponseDto.builder().customerId(1L).build();
    when(customerRepository.findByPhone("0123")).thenReturn(Optional.of(customer));
    when(customerMapper.toDetailDto(customer)).thenReturn(dto);

    CustomerDetailResponseDto result = customerServiceImpl.getByPhone("0123");
    assertEquals(1L, result.getCustomerId());
  }

  @Test
  void getByPhone_WhenCustomerNotFound_ShouldThrowException() {
    when(customerRepository.findByPhone("0123")).thenReturn(Optional.empty());
    assertThrows(ResourceNotFoundException.class, () -> customerServiceImpl.getByPhone("0123"));
  }

  @Test
  void createCustomer_WhenPhoneExists_ShouldThrowException() {
    CustomerRequestDto req = CustomerRequestDto.builder().phone("0123").build();
    when(customerRepository.existsByPhone("0123")).thenReturn(true);
    assertThrows(RuntimeException.class, () -> customerServiceImpl.createCustomer(req));
  }

  @Test
  void createCustomer_WhenValid_ShouldSaveAndReturnDto() {
    CustomerRequestDto req = CustomerRequestDto.builder().phone("0123").build();
    Customer customer = Customer.builder().customerId(1L).build();
    CustomerResponseDto dto = CustomerResponseDto.builder().customerId(1L).build();
    when(customerRepository.existsByPhone("0123")).thenReturn(false);
    when(customerMapper.toEntity(req)).thenReturn(customer);
    when(customerRepository.save(customer)).thenReturn(customer);
    when(customerMapper.toDto(customer)).thenReturn(dto);

    CustomerResponseDto result = customerServiceImpl.createCustomer(req);
    assertEquals(1L, result.getCustomerId());
  }

  @Test
  void updateCustomer_WhenCustomerNotFound_ShouldThrowException() {
    CustomerRequestDto req = CustomerRequestDto.builder().phone("0123").discountPolicyId(1L).build();
    when(customerRepository.findById(99L)).thenReturn(Optional.empty());
    assertThrows(ResourceNotFoundException.class, () -> customerServiceImpl.updateCustomer(99L, req));
  }

  @Test
  void updateCustomer_WhenPhoneChangedAndExists_ShouldThrowException() {
    Customer existing = Customer.builder().customerId(1L).phone("old").build();
    CustomerRequestDto req = CustomerRequestDto.builder().phone("new").discountPolicyId(1L).build();
    when(customerRepository.findById(1L)).thenReturn(Optional.of(existing));
    when(customerRepository.existsByPhone("new")).thenReturn(true);

    assertThrows(RuntimeException.class, () -> customerServiceImpl.updateCustomer(1L, req));
  }

  @Test
  void updateCustomer_WhenDiscountPolicyNotFound_ShouldThrowException() {
    Customer existing = Customer.builder().customerId(1L).phone("0123").build();
    CustomerRequestDto req = CustomerRequestDto.builder().phone("0123").discountPolicyId(2L).build();
    when(customerRepository.findById(1L)).thenReturn(Optional.of(existing));
    when(discountPolicyRepository.findById(2L)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> customerServiceImpl.updateCustomer(1L, req));
  }

  @Test
  void updateCustomer_WhenValid_ShouldUpdateAndReturnDto() {
    Customer existing = Customer.builder().customerId(1L).phone("0123").build();
    CustomerRequestDto req = CustomerRequestDto.builder()
        .fullName("A")
        .phone("0123")
        .address("Addr")
        .customerType(CustomerType.CA_NHAN)
        .discountPolicyId(2L)
        .build();
    DiscountPolicy policy = DiscountPolicy.builder().discountPolicyId(2L).build();
    Customer updated = Customer.builder().customerId(1L).phone("0123").build();
    CustomerResponseDto dto = CustomerResponseDto.builder().customerId(1L).build();

    when(customerRepository.findById(1L)).thenReturn(Optional.of(existing));
    when(discountPolicyRepository.findById(2L)).thenReturn(Optional.of(policy));
    when(customerRepository.save(any(Customer.class))).thenReturn(updated);
    when(customerMapper.toDto(updated)).thenReturn(dto);

    CustomerResponseDto result = customerServiceImpl.updateCustomer(1L, req);
    assertEquals(1L, result.getCustomerId());
  }

  @Test
  void getCustomerServiceHistoryByPhone_WhenCustomerNotFound_ShouldThrowException() {
    when(customerRepository.findByPhone("0123")).thenReturn(Optional.empty());
    assertThrows(ResourceNotFoundException.class, () -> customerServiceImpl.getCustomerServiceHistoryByPhone("0123"));
  }

  @Test
  void getCustomerServiceHistoryByPhone_WhenHasCompletedTickets_ShouldReturnHistoryDto() {
    Customer customer = Customer.builder().customerId(1L).fullName("A").phone("0123").build();
    VehicleModel model = VehicleModel.builder().name("ModelX").brand(Brand.builder().name("BrandY").build()).build();
    Vehicle vehicle = Vehicle.builder().licensePlate("ABC123").vehicleModel(model).build();
    ServiceTicket ticket = ServiceTicket.builder()
        .serviceTicketId(1L)
        .customer(customer)
        .vehicle(vehicle)
        .status(ServiceTicketStatus.COMPLETED)
        .createdAt(LocalDateTime.now())
        .build();
    when(customerRepository.findByPhone("0123")).thenReturn(Optional.of(customer));
    when(serviceTicketRepository.findAll()).thenReturn(List.of(ticket));

    CustomerServiceHistoryResponseDto result = customerServiceImpl.getCustomerServiceHistoryByPhone("0123");
    assertEquals("A", result.getFullName());
    assertEquals("0123", result.getPhone());
    assertEquals(1, result.getVehicles().size());
    assertEquals("ABC123", result.getVehicles().get(0).getLicensePlate());
    assertEquals("ModelX", result.getVehicles().get(0).getModelName());
    assertEquals("BrandY", result.getVehicles().get(0).getBrandName());
  }

  @Test
  void updateTotalSpending_WhenCustomerNotFound_ShouldThrowException() {
    when(customerRepository.findById(99L)).thenReturn(Optional.empty());
    assertThrows(ResourceNotFoundException.class, () -> customerServiceImpl.updateTotalSpending(99L, BigDecimal.TEN));
  }

  @Test
  void updateTotalSpending_WhenValid_ShouldUpdateSpendingAndPolicy() {
    DiscountPolicy p1 = DiscountPolicy.builder().discountPolicyId(1L).requiredSpending(BigDecimal.valueOf(100)).build();
    DiscountPolicy p2 = DiscountPolicy.builder().discountPolicyId(2L).requiredSpending(BigDecimal.valueOf(200)).build();
    Customer customer = Customer.builder()
        .customerId(1L)
        .totalSpending(BigDecimal.valueOf(150))
        .discountPolicy(p1)
        .build();
    when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
    when(discountPolicyRepository.findAll()).thenReturn(List.of(p1, p2));
    when(customerRepository.save(any(Customer.class))).thenReturn(customer);

    customerServiceImpl.updateTotalSpending(1L, BigDecimal.valueOf(100));
    assertEquals(BigDecimal.valueOf(250), customer.getTotalSpending());
    assertEquals(p2, customer.getDiscountPolicy());
  }

  @Test
  void getCustomers_WhenCalled_ShouldReturnPagedList() {
    CustomerListResponseDto dto = CustomerListResponseDto.builder().customerId(1L).build();
    Page<CustomerListResponseDto> page = new PageImpl<>(List.of(dto));
    when(customerRepository.getAllCustomers(any(Pageable.class))).thenReturn(page);

    Page<CustomerListResponseDto> result = customerServiceImpl.getCustomers(0, 10);
    assertEquals(1, result.getTotalElements());
    assertEquals(1L, result.getContent().get(0).getCustomerId());
  }

  @Test
  void getCustomerDetail_WhenCalled_ShouldReturnDetailWithVehicles() {
    CustomerDetailDto dto = CustomerDetailDto.builder().customerId(1L).build();
    List<VehicleInfoDto> vehicles = List
        .of(VehicleInfoDto.builder().licensePlate("ABC123").build());
    when(customerRepository.getCustomerDetail(1L)).thenReturn(dto);
    when(vehicleRepository.getCustomerVehicles(1L)).thenReturn(vehicles);

    CustomerDetailDto result = customerServiceImpl.getCustomerDetail(1L);
    assertEquals(1L, result.getCustomerId());
    assertEquals(vehicles, result.getVehicles());
    assertNull(result.getHistory());
  }

  @Test
  void getServiceHistory_WhenCalled_ShouldReturnDetailWithHistory() {
    CustomerDetailDto dto = CustomerDetailDto.builder().customerId(1L).build();
    List<CustomerServiceHistoryDto> history = List
        .of(CustomerServiceHistoryDto.builder().serviceTicketCode("1").build());
    when(customerRepository.getCustomerDetail(1L)).thenReturn(dto);
    when(serviceTicketRepository.getCustomerServiceHistory(1L)).thenReturn(history);

    CustomerDetailDto result = customerServiceImpl.getServiceHistory(1L);
    assertEquals(1L, result.getCustomerId());
    assertEquals(history, result.getHistory());
    assertNull(result.getVehicles());
  }
}
