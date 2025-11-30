package fpt.edu.vn.gms.service.impl;

import fpt.edu.vn.gms.base.BaseServiceTest;
import fpt.edu.vn.gms.common.enums.CustomerLoyaltyLevel;
import fpt.edu.vn.gms.common.enums.Role;
import fpt.edu.vn.gms.common.enums.ServiceTicketStatus;
import fpt.edu.vn.gms.dto.request.CustomerRequestDto;
import fpt.edu.vn.gms.dto.request.ServiceTicketRequestDto;
import fpt.edu.vn.gms.dto.request.TicketUpdateReqDto;
import fpt.edu.vn.gms.dto.request.VehicleRequestDto;
import fpt.edu.vn.gms.dto.response.ServiceTicketResponseDto;
import fpt.edu.vn.gms.entity.*;
import fpt.edu.vn.gms.exception.ResourceNotFoundException;
import fpt.edu.vn.gms.mapper.ServiceTicketMapper;
import fpt.edu.vn.gms.repository.*;
import fpt.edu.vn.gms.service.CodeSequenceService;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class ServiceTicketServiceImplTest extends BaseServiceTest {

  @Mock
  private ServiceTicketRepository serviceTicketRepository;
  @Mock
  private AppointmentRepository appointmentRepository;
  @Mock
  private CustomerRepository customerRepository;
  @Mock
  private VehicleRepository vehicleRepository;
  @Mock
  private EmployeeRepository employeeRepository;
  @Mock
  private ServiceTypeRepository serviceTypeRepository;
  @Mock
  private ServiceTicketMapper serviceTicketMapper;
  @Mock
  private VehicleModelRepository vehicleModelRepository;
  @Mock
  private DiscountPolicyRepository discountPolicyRepo;
  @Mock
  private CodeSequenceService codeSequenceService;
  @Mock
  private BrandRepository brandRepository;

  @InjectMocks
  private ServiceTicketServiceImpl serviceTicketServiceImpl;

  @Test
  void createServiceTicket_WhenCustomerAndVehicleExist_ShouldCreateTicket() {
    Employee emp = getMockEmployee(Role.SERVICE_ADVISOR);
    Customer customer = Customer.builder().customerId(1L).fullName("A").phone("0123").address("B").build();
    Vehicle vehicle = Vehicle.builder().vehicleId(2L).licensePlate("ABC").customer(customer).build();
    CustomerRequestDto customerDto = CustomerRequestDto.builder()
        .customerId(1L)
        .fullName("A")
        .phone("0123")
        .address("B")
        .build();
    VehicleRequestDto vehicleDto = VehicleRequestDto.builder()
        .vehicleId(2L)
        .licensePlate("ABC")
        .build();
    ServiceTicketRequestDto dto = ServiceTicketRequestDto.builder()
        .customer(customerDto)
        .vehicle(vehicleDto)
        .receiveCondition("Good")
        .expectedDeliveryAt(LocalDate.now())
        .build();

    when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
    when(vehicleRepository.findById(2L)).thenReturn(Optional.of(vehicle));
    when(codeSequenceService.generateCode("STK")).thenReturn("STK-001");
    ServiceTicket ticket = ServiceTicket.builder().serviceTicketId(10L).customer(customer).vehicle(vehicle).build();
    when(serviceTicketRepository.save(any(ServiceTicket.class))).thenReturn(ticket);
    ServiceTicketResponseDto responseDto = ServiceTicketResponseDto.builder().serviceTicketId(10L).build();
    when(serviceTicketMapper.toResponseDto(ticket)).thenReturn(responseDto);

    ServiceTicketResponseDto result = serviceTicketServiceImpl.createServiceTicket(dto, emp);

    assertNotNull(result);
    assertEquals(10L, result.getServiceTicketId());
    verify(serviceTicketRepository).save(any(ServiceTicket.class));
  }

  @Test
  void createServiceTicket_WhenCustomerNotExist_ShouldCreateNewCustomer() {
    Employee emp = getMockEmployee(Role.SERVICE_ADVISOR);
    DiscountPolicy policy = DiscountPolicy.builder().discountPolicyId(1L).loyaltyLevel(CustomerLoyaltyLevel.BRONZE)
        .discountRate(BigDecimal.valueOf(5)).build();
    // CustomerRequestDto không có customerId (tạo mới)
    CustomerRequestDto customerDto = CustomerRequestDto.builder()
        .fullName("B")
        .phone("0999")
        .address("C")
        .customerType(null)
        .build();
    // VehicleRequestDto không có vehicleId (tạo mới)
    VehicleRequestDto vehicleDto = VehicleRequestDto.builder()
        .licensePlate("XYZ")
        .year(2020)
        .vin("VIN123")
        .brandId(null)
        .brandName("BrandX")
        .modelId(null)
        .modelName("ModelY")
        .build();
    ServiceTicketRequestDto dto = ServiceTicketRequestDto.builder()
        .customer(customerDto)
        .vehicle(vehicleDto)
        .receiveCondition("Good")
        .expectedDeliveryAt(LocalDate.now())
        .build();
    Customer newCustomer = Customer.builder().customerId(2L).fullName("B").phone("0999").address("C")
        .discountPolicy(policy).isActive(true).build();
    Brand brand = Brand.builder().brandId(1L).name("BrandX").build();
    VehicleModel model = VehicleModel.builder().vehicleModelId(1L).name("ModelY").brand(brand).build();
    Vehicle vehicle = Vehicle.builder().vehicleId(3L).licensePlate("XYZ").vehicleModel(model).year(2020).vin("VIN123")
        .customer(newCustomer).build();
    ServiceTicket ticket = ServiceTicket.builder().serviceTicketId(11L).customer(newCustomer).vehicle(vehicle).build();
    ServiceTicketResponseDto responseDto = ServiceTicketResponseDto.builder().serviceTicketId(11L).build();

    // 1. DiscountPolicy mặc định
    when(discountPolicyRepo.findByLoyaltyLevel(CustomerLoyaltyLevel.BRONZE)).thenReturn(Optional.of(policy));
    // 2. Customer chưa có customerId nên sẽ save mới
    when(customerRepository.save(any(Customer.class))).thenReturn(newCustomer);
    // 3. Vehicle chưa có vehicleId, tìm theo licensePlate
    when(vehicleRepository.findByLicensePlate("XYZ")).thenReturn(Optional.empty());
    // 4. Brand chưa có brandId, sẽ save mới
    when(brandRepository.save(any(Brand.class))).thenReturn(brand);
    // 5. VehicleModel chưa có modelId, sẽ save mới
    when(vehicleModelRepository.save(any(VehicleModel.class))).thenReturn(model);
    // 6. Vehicle sẽ save mới
    when(vehicleRepository.save(any(Vehicle.class))).thenReturn(vehicle);
    // 7. Sinh mã code
    when(codeSequenceService.generateCode("STK")).thenReturn("STK-002");
    // 8. Lưu ServiceTicket
    when(serviceTicketRepository.save(any(ServiceTicket.class))).thenReturn(ticket);
    // 9. Mapper trả về response
    when(serviceTicketMapper.toResponseDto(ticket)).thenReturn(responseDto);

    ServiceTicketResponseDto result = serviceTicketServiceImpl.createServiceTicket(dto, emp);

    assertNotNull(result);
    assertEquals(11L, result.getServiceTicketId());
    verify(customerRepository).save(any(Customer.class));
    verify(vehicleRepository).save(any(Vehicle.class));
  }

  @Test
  void getServiceTicketById_WhenTicketExists_ShouldReturnDto() {
    ServiceTicket ticket = ServiceTicket.builder().serviceTicketId(1L).build();
    ServiceTicketResponseDto dto = ServiceTicketResponseDto.builder().serviceTicketId(1L).build();
    when(serviceTicketRepository.findById(1L)).thenReturn(Optional.of(ticket));
    when(serviceTicketMapper.toResponseDto(ticket)).thenReturn(dto);

    ServiceTicketResponseDto result = serviceTicketServiceImpl.getServiceTicketById(1L);

    assertNotNull(result);
    assertEquals(1L, result.getServiceTicketId());
  }

  @Test
  void getServiceTicketById_WhenNotFound_ShouldThrowResourceNotFoundException() {
    when(serviceTicketRepository.findById(99L)).thenReturn(Optional.empty());
    assertThrows(ResourceNotFoundException.class, () -> serviceTicketServiceImpl.getServiceTicketById(99L));
  }

  @Test
  void getAllServiceTicket_WhenTicketsExist_ShouldReturnPagedDtos() {
    ServiceTicket ticket = ServiceTicket.builder().serviceTicketId(1L).build();
    ServiceTicketResponseDto dto = ServiceTicketResponseDto.builder().serviceTicketId(1L).build();
    Page<ServiceTicket> page = new PageImpl<>(List.of(ticket));
    when(serviceTicketRepository.findAll(any(Pageable.class))).thenReturn(page);
    when(serviceTicketMapper.toResponseDto(ticket)).thenReturn(dto);

    Page<ServiceTicketResponseDto> result = serviceTicketServiceImpl.getAllServiceTicket(0, 10);

    assertEquals(1, result.getTotalElements());
    assertEquals(1L, result.getContent().get(0).getServiceTicketId());
  }

  @Test
  void updateServiceTicket_WhenTicketExists_ShouldUpdateAndReturnDto() {
    ServiceTicket ticket = ServiceTicket.builder()
        .serviceTicketId(1L)
        .customer(Customer.builder().customerId(1L).fullName("Old Name").phone("0123").build())
        .vehicle(Vehicle.builder().vehicleId(2L).licensePlate("ABC").build())
        .build();
    TicketUpdateReqDto dto = TicketUpdateReqDto.builder()
        .customerName("New Name")
        .customerPhone("0999")
        .vehicleId(2L)
        .assignedTechnicianId(List.of(1L))
        .serviceTypeIds(List.of(1L, 2L))
        .build();
    Employee tech = getMockEmployee(Role.SERVICE_ADVISOR);
    ServiceType st1 = ServiceType.builder().id(1L).build();
    ServiceType st2 = ServiceType.builder().id(2L).build();

    when(serviceTicketRepository.findById(1L)).thenReturn(Optional.of(ticket));
    when(vehicleRepository.findById(2L)).thenReturn(Optional.of(ticket.getVehicle()));
    when(employeeRepository.findAllById(List.of(1L))).thenReturn(List.of(tech));
    when(serviceTypeRepository.findAllById(List.of(1L, 2L))).thenReturn(List.of(st1, st2));
    when(serviceTicketRepository.save(any(ServiceTicket.class))).thenReturn(ticket);
    ServiceTicketResponseDto responseDto = ServiceTicketResponseDto.builder().serviceTicketId(1L).build();
    when(serviceTicketMapper.toResponseDto(ticket)).thenReturn(responseDto);

    ServiceTicketResponseDto result = serviceTicketServiceImpl.updateServiceTicket(1L, dto);

    assertNotNull(result);
    assertEquals(1L, result.getServiceTicketId());
    assertEquals("New Name", ticket.getCustomer().getFullName());
    verify(serviceTicketRepository).save(ticket);
  }

  @Test
  void updateServiceTicket_WhenTicketNotFound_ShouldThrowResourceNotFoundException() {
    TicketUpdateReqDto dto = TicketUpdateReqDto.builder().build();
    when(serviceTicketRepository.findById(99L)).thenReturn(Optional.empty());
    assertThrows(ResourceNotFoundException.class, () -> serviceTicketServiceImpl.updateServiceTicket(99L, dto));
  }

  @Test
  void updateDeliveryAt_WhenTicketExists_ShouldUpdateAndReturnDto() {
    ServiceTicket ticket = ServiceTicket.builder().serviceTicketId(1L).deliveryAt(LocalDate.now()).build();
    ServiceTicketResponseDto dto = ServiceTicketResponseDto.builder().serviceTicketId(1L).build();
    when(serviceTicketRepository.findById(1L)).thenReturn(Optional.of(ticket));
    when(serviceTicketRepository.save(ticket)).thenReturn(ticket);
    when(serviceTicketMapper.toResponseDto(ticket)).thenReturn(dto);

    ServiceTicketResponseDto result = serviceTicketServiceImpl.updateDeliveryAt(1L, LocalDate.now().plusDays(1));

    assertNotNull(result);
    verify(serviceTicketRepository).save(ticket);
  }

  @Test
  void updateDeliveryAt_WhenTicketNotFound_ShouldThrowResourceNotFoundException() {
    when(serviceTicketRepository.findById(99L)).thenReturn(Optional.empty());
    assertThrows(ResourceNotFoundException.class,
        () -> serviceTicketServiceImpl.updateDeliveryAt(99L, LocalDate.now()));
  }

  @Test
  void getServiceTicketsByStatus_WhenTicketsExist_ShouldReturnPagedDtos() {
    ServiceTicket ticket = ServiceTicket.builder().serviceTicketId(1L).build();
    ServiceTicketResponseDto dto = ServiceTicketResponseDto.builder().serviceTicketId(1L).build();
    Page<ServiceTicket> page = new PageImpl<>(List.of(ticket));
    when(serviceTicketRepository.findByStatus(eq(ServiceTicketStatus.CREATED), any(Pageable.class))).thenReturn(page);
    when(serviceTicketMapper.toResponseDto(ticket)).thenReturn(dto);

    Page<ServiceTicketResponseDto> result = serviceTicketServiceImpl
        .getServiceTicketsByStatus(ServiceTicketStatus.CREATED, 0, 10);

    assertEquals(1, result.getTotalElements());
    assertEquals(1L, result.getContent().get(0).getServiceTicketId());
  }

  @Test
  void countServiceTicketByDate_WhenCalled_ShouldReturnCount() {
    when(serviceTicketRepository.countByDate(any(LocalDate.class))).thenReturn(5L);
    long result = serviceTicketServiceImpl.countServiceTicketByDate(LocalDate.now());
    assertEquals(5L, result);
  }

  @Test
  void getCompletedTicketsByMonth_WhenCalled_ShouldReturnListOfMap() {
    Object[] row = new Object[] { 2024, 5, 10L };
    when(serviceTicketRepository.countCompletedTicketsGroupedByMonth("COMPLETED")).thenReturn(List.<Object[]>of(row));

    List<Map<String, Object>> result = serviceTicketServiceImpl.getCompletedTicketsByMonth();

    assertEquals(1, result.size());
    assertEquals(2024, result.get(0).get("year"));
    assertEquals(5, result.get(0).get("month"));
    assertEquals(10L, result.get(0).get("count"));
  }

  @Test
  void getTicketCountsByType_WhenCalled_ShouldReturnListOfMap() {
    Object[] row = new Object[] { "Bảo dưỡng", 3L };
    when(serviceTicketRepository.countTicketsByTypeForMonth(2024, 5)).thenReturn(List.<Object[]>of(row));

    List<Map<String, Object>> result = serviceTicketServiceImpl.getTicketCountsByType(2024, 5);

    assertEquals(1, result.size());
    assertEquals("Bảo dưỡng", result.get(0).get("type"));
    assertEquals(3L, result.get(0).get("count"));
  }
}
