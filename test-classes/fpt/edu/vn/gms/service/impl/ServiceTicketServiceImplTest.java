package fpt.edu.vn.gms.service.impl;

import fpt.edu.vn.gms.common.enums.CustomerLoyaltyLevel;
import fpt.edu.vn.gms.common.enums.CustomerType;
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
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServiceTicketServiceImplTest {

    @Mock
    ServiceTicketRepository serviceTicketRepository;
    @Mock
    AppointmentRepository appointmentRepository;
    @Mock
    CustomerRepository customerRepository;
    @Mock
    VehicleRepository vehicleRepository;
    @Mock
    EmployeeRepository employeeRepository;
    @Mock
    ServiceTypeRepository serviceTypeRepository;
    @Mock
    ServiceTicketMapper serviceTicketMapper;
    @Mock
    VehicleModelRepository vehicleModelRepository;
    @Mock
    DiscountPolicyRepository discountPolicyRepo;
    @Mock
    CodeSequenceService codeSequenceService;
    @Mock
    BrandRepository brandRepository;

    @InjectMocks
    ServiceTicketServiceImpl service;

    @Test
    void createServiceTicket_ShouldCreateWithExistingCustomerAndVehicle() {
        Employee currEmployee = Employee.builder()
                .employeeId(1L)
                .fullName("Employee")
                .build();

        Customer customer = Customer.builder()
                .customerId(1L)
                .fullName("Customer")
                .phone("0912345678")
                .build();
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        Brand brand = Brand.builder().brandId(1L).name("Toyota").build();
        VehicleModel model = VehicleModel.builder()
                .vehicleModelId(1L)
                .name("Camry")
                .brand(brand)
                .build();
        Vehicle vehicle = Vehicle.builder()
                .vehicleId(1L)
                .licensePlate("30A-12345")
                .vehicleModel(model)
                .customer(customer)
                .build();
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicle));
        when(brandRepository.findById(1L)).thenReturn(Optional.of(brand));
        when(vehicleModelRepository.findById(1L)).thenReturn(Optional.of(model));

        when(codeSequenceService.generateCode("STK")).thenReturn("STK-2025-00001");

        ServiceTicket savedTicket = ServiceTicket.builder()
                .serviceTicketId(100L)
                .serviceTicketCode("STK-2025-00001")
                .customer(customer)
                .vehicle(vehicle)
                .createdBy(currEmployee)
                .status(ServiceTicketStatus.CREATED)
                .build();
        when(serviceTicketRepository.save(any(ServiceTicket.class))).thenReturn(savedTicket);

        ServiceTicketResponseDto dto = ServiceTicketResponseDto.builder()
                .serviceTicketId(100L)
                .serviceTicketCode("STK-2025-00001")
                .build();
        when(serviceTicketMapper.toResponseDto(savedTicket)).thenReturn(dto);

        CustomerRequestDto customerDto = CustomerRequestDto.builder()
                .customerId(1L)
                .address("New Address")
                .build();
        VehicleRequestDto vehicleDto = VehicleRequestDto.builder()
                .vehicleId(1L)
                .licensePlate("30A-12345")
                .brandId(1L)
                .modelId(1L)
                .year(2020)
                .vin("VIN123")
                .build();
        ServiceTicketRequestDto request = ServiceTicketRequestDto.builder()
                .customer(customerDto)
                .vehicle(vehicleDto)
                .build();

        ServiceTicketResponseDto result = service.createServiceTicket(request, currEmployee);

        assertSame(dto, result);
        assertEquals("New Address", customer.getAddress());
        verify(customerRepository).findById(1L);
        verify(vehicleRepository).findById(1L);
        verify(serviceTicketRepository).save(any(ServiceTicket.class));
    }

    @Test
    void createServiceTicket_ShouldCreateNewCustomer_WhenCustomerIdIsNull() {
        Employee currEmployee = Employee.builder()
                .employeeId(1L)
                .build();

        DiscountPolicy defaultPolicy = DiscountPolicy.builder()
                .discountPolicyId(1L)
                .loyaltyLevel(CustomerLoyaltyLevel.BRONZE)
                .build();
        when(discountPolicyRepo.findByLoyaltyLevel(CustomerLoyaltyLevel.BRONZE))
                .thenReturn(Optional.of(defaultPolicy));

        Customer savedCustomer = Customer.builder()
                .customerId(100L)
                .fullName("New Customer")
                .phone("0912345678")
                .build();
        when(customerRepository.save(any(Customer.class))).thenReturn(savedCustomer);

        Brand brand = Brand.builder().brandId(1L).name("Toyota").build();
        when(brandRepository.save(any(Brand.class))).thenReturn(brand);

        VehicleModel model = VehicleModel.builder()
                .vehicleModelId(1L)
                .name("Camry")
                .brand(brand)
                .build();
        when(vehicleModelRepository.save(any(VehicleModel.class))).thenReturn(model);

        Vehicle savedVehicle = Vehicle.builder()
                .vehicleId(200L)
                .licensePlate("30A-12345")
                .customer(savedCustomer)
                .build();
        when(vehicleRepository.findByLicensePlate("30A-12345")).thenReturn(Optional.empty());
        when(vehicleRepository.save(any(Vehicle.class))).thenReturn(savedVehicle);

        when(codeSequenceService.generateCode("STK")).thenReturn("STK-2025-00001");

        ServiceTicket savedTicket = ServiceTicket.builder()
                .serviceTicketId(300L)
                .serviceTicketCode("STK-2025-00001")
                .build();
        when(serviceTicketRepository.save(any(ServiceTicket.class))).thenReturn(savedTicket);

        ServiceTicketResponseDto dto = ServiceTicketResponseDto.builder()
                .serviceTicketId(300L)
                .build();
        when(serviceTicketMapper.toResponseDto(savedTicket)).thenReturn(dto);

        CustomerRequestDto customerDto = CustomerRequestDto.builder()
                .customerId(null)
                .fullName("New Customer")
                .phone("0912345678")
                .customerType(CustomerType.CA_NHAN)
                .build();
        VehicleRequestDto vehicleDto = VehicleRequestDto.builder()
                .licensePlate("30A-12345")
                .brandName("Toyota")
                .modelName("Camry")
                .year(2020)
                .build();
        ServiceTicketRequestDto request = ServiceTicketRequestDto.builder()
                .customer(customerDto)
                .vehicle(vehicleDto)
                .build();

        ServiceTicketResponseDto result = service.createServiceTicket(request, currEmployee);

        assertNotNull(result);
        verify(discountPolicyRepo).findByLoyaltyLevel(CustomerLoyaltyLevel.BRONZE);
        verify(customerRepository).save(any(Customer.class));
        verify(vehicleRepository).save(any(Vehicle.class));
    }

    @Test
    void createServiceTicket_ShouldThrow_WhenCustomerNotFound() {
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());

        CustomerRequestDto customerDto = CustomerRequestDto.builder()
                .customerId(1L)
                .build();
        VehicleRequestDto vehicleDto = VehicleRequestDto.builder()
                .vehicleId(1L)
                .licensePlate("30A-12345")
                .build();
        ServiceTicketRequestDto request = ServiceTicketRequestDto.builder()
                .customer(customerDto)
                .vehicle(vehicleDto)
                .build();

        Employee currEmployee = Employee.builder().employeeId(1L).build();

        assertThrows(ResourceNotFoundException.class,
                () -> service.createServiceTicket(request, currEmployee));
        verify(customerRepository).findById(1L);
    }

    @Test
    void createServiceTicket_ShouldThrow_WhenVehicleNotFound() {
        Customer customer = Customer.builder().customerId(1L).build();
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(vehicleRepository.findById(1L)).thenReturn(Optional.empty());

        CustomerRequestDto customerDto = CustomerRequestDto.builder()
                .customerId(1L)
                .build();
        VehicleRequestDto vehicleDto = VehicleRequestDto.builder()
                .vehicleId(1L)
                .licensePlate("30A-12345")
                .build();
        ServiceTicketRequestDto request = ServiceTicketRequestDto.builder()
                .customer(customerDto)
                .vehicle(vehicleDto)
                .build();

        Employee currEmployee = Employee.builder().employeeId(1L).build();

        assertThrows(ResourceNotFoundException.class,
                () -> service.createServiceTicket(request, currEmployee));
        verify(vehicleRepository).findById(1L);
    }

    @Test
    void createServiceTicket_ShouldThrow_WhenVehicleBelongsToOtherCustomerAndNotForceAssign() {
        Customer customer = Customer.builder()
                .customerId(1L)
                .fullName("Customer 1")
                .build();
        Customer otherCustomer = Customer.builder()
                .customerId(2L)
                .fullName("Customer 2")
                .build();

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        Brand brand = Brand.builder().brandId(1L).name("Toyota").build();
        VehicleModel model = VehicleModel.builder()
                .vehicleModelId(1L)
                .name("Camry")
                .brand(brand)
                .build();
        Vehicle vehicle = Vehicle.builder()
                .vehicleId(1L)
                .licensePlate("30A-12345")
                .customer(otherCustomer)
                .vehicleModel(model)
                .build();
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(vehicle));
        when(brandRepository.findById(1L)).thenReturn(Optional.of(brand));
        when(vehicleModelRepository.findById(1L)).thenReturn(Optional.of(model));

        CustomerRequestDto customerDto = CustomerRequestDto.builder()
                .customerId(1L)
                .build();
        VehicleRequestDto vehicleDto = VehicleRequestDto.builder()
                .vehicleId(1L)
                .licensePlate("30A-12345")
                .brandId(1L)
                .modelId(1L)
                .build();
        ServiceTicketRequestDto request = ServiceTicketRequestDto.builder()
                .customer(customerDto)
                .vehicle(vehicleDto)
                .forceAssignVehicle(false)
                .build();

        Employee currEmployee = Employee.builder().employeeId(1L).build();

        assertThrows(RuntimeException.class,
                () -> service.createServiceTicket(request, currEmployee));
    }

    @Test
    void getServiceTicketById_ShouldReturnDto_WhenFound() {
        ServiceTicket ticket = ServiceTicket.builder()
                .serviceTicketId(1L)
                .serviceTicketCode("STK-2025-00001")
                .build();
        when(serviceTicketRepository.findDetail(1L)).thenReturn(Optional.of(ticket));

        ServiceTicketResponseDto dto = ServiceTicketResponseDto.builder()
                .serviceTicketId(1L)
                .serviceTicketCode("STK-2025-00001")
                .build();
        when(serviceTicketMapper.toResponseDto(ticket)).thenReturn(dto);

        ServiceTicketResponseDto result = service.getServiceTicketById(1L);

        assertSame(dto, result);
        verify(serviceTicketRepository).findDetail(1L);
    }

    @Test
    void getServiceTicketById_ShouldThrow_WhenNotFound() {
        when(serviceTicketRepository.findDetail(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> service.getServiceTicketById(1L));
        verify(serviceTicketRepository).findDetail(1L);
    }

    @Test
    void getAllServiceTicket_ShouldReturnPagedDtos() {
        Pageable pageable = PageRequest.of(0, 5);
        ServiceTicket ticket = ServiceTicket.builder()
                .serviceTicketId(1L)
                .serviceTicketCode("STK-2025-00001")
                .build();
        Page<ServiceTicket> page = new PageImpl<>(List.of(ticket), pageable, 1);

        when(serviceTicketRepository.findAll(pageable)).thenReturn(page);

        ServiceTicketResponseDto dto = ServiceTicketResponseDto.builder()
                .serviceTicketId(1L)
                .serviceTicketCode("STK-2025-00001")
                .build();
        when(serviceTicketMapper.toResponseDto(ticket)).thenReturn(dto);

        Page<ServiceTicketResponseDto> result = service.getAllServiceTicket(0, 5);

        assertEquals(1, result.getTotalElements());
        assertSame(dto, result.getContent().get(0));
        verify(serviceTicketRepository).findAll(any(Pageable.class));
    }

    @Test
    void updateServiceTicket_ShouldUpdateTechniciansAndServiceTypes() {
        ServiceTicket existing = ServiceTicket.builder()
                .serviceTicketId(1L)
                .serviceTicketCode("STK-2025-00001")
                .technicians(new ArrayList<>())
                .serviceTypes(new ArrayList<>())
                .build();
        when(serviceTicketRepository.findById(1L)).thenReturn(Optional.of(existing));

        Employee tech1 = Employee.builder().employeeId(10L).fullName("Tech 1").build();
        Employee tech2 = Employee.builder().employeeId(11L).fullName("Tech 2").build();
        when(employeeRepository.findAllById(List.of(10L, 11L))).thenReturn(List.of(tech1, tech2));

        ServiceType type1 = ServiceType.builder().id(20L).name("Type 1").build();
        ServiceType type2 = ServiceType.builder().id(21L).name("Type 2").build();
        when(serviceTypeRepository.findAllById(List.of(20L, 21L))).thenReturn(List.of(type1, type2));

        when(serviceTicketRepository.save(existing)).thenReturn(existing);

        ServiceTicketResponseDto dto = ServiceTicketResponseDto.builder()
                .serviceTicketId(1L)
                .build();
        when(serviceTicketMapper.toResponseDto(existing)).thenReturn(dto);

        TicketUpdateReqDto updateDto = TicketUpdateReqDto.builder()
                .assignedTechnicianId(List.of(10L, 11L))
                .serviceTypeIds(List.of(20L, 21L))
                .deliveryAt(LocalDate.now().plusDays(5))
                .build();

        ServiceTicketResponseDto result = service.updateServiceTicket(1L, updateDto);

        assertSame(dto, result);
        assertEquals(2, existing.getTechnicians().size());
        assertEquals(2, existing.getServiceTypes().size());
        verify(serviceTicketRepository).save(existing);
    }

    @Test
    void updateServiceTicket_ShouldThrow_WhenNotFound() {
        when(serviceTicketRepository.findById(1L)).thenReturn(Optional.empty());

        TicketUpdateReqDto updateDto = TicketUpdateReqDto.builder().build();

        assertThrows(ResourceNotFoundException.class,
                () -> service.updateServiceTicket(1L, updateDto));
        verify(serviceTicketRepository).findById(1L);
        verify(serviceTicketRepository, never()).save(any());
    }

    @Test
    void updateDeliveryAt_ShouldUpdateAndReturnDto() {
        ServiceTicket ticket = ServiceTicket.builder()
                .serviceTicketId(1L)
                .serviceTicketCode("STK-2025-00001")
                .build();
        when(serviceTicketRepository.findById(1L)).thenReturn(Optional.of(ticket));

        LocalDate newDeliveryAt = LocalDate.now().plusDays(7);
        when(serviceTicketRepository.save(ticket)).thenReturn(ticket);

        ServiceTicketResponseDto dto = ServiceTicketResponseDto.builder()
                .serviceTicketId(1L)
                .deliveryAt(newDeliveryAt)
                .build();
        when(serviceTicketMapper.toResponseDto(ticket)).thenReturn(dto);

        ServiceTicketResponseDto result = service.updateDeliveryAt(1L, newDeliveryAt);

        assertSame(dto, result);
        assertEquals(newDeliveryAt, ticket.getDeliveryAt());
        verify(serviceTicketRepository).save(ticket);
    }

    @Test
    void updateDeliveryAt_ShouldThrow_WhenNotFound() {
        when(serviceTicketRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> service.updateDeliveryAt(1L, LocalDate.now()));
        verify(serviceTicketRepository).findById(1L);
        verify(serviceTicketRepository, never()).save(any());
    }

    @Test
    void getServiceTicketsByStatus_ShouldReturnPagedDtos() {
        Pageable pageable = PageRequest.of(0, 5);
        ServiceTicket ticket = ServiceTicket.builder()
                .serviceTicketId(1L)
                .status(ServiceTicketStatus.CREATED)
                .build();
        Page<ServiceTicket> page = new PageImpl<>(List.of(ticket), pageable, 1);

        when(serviceTicketRepository.findByStatus(ServiceTicketStatus.CREATED, pageable))
                .thenReturn(page);

        ServiceTicketResponseDto dto = ServiceTicketResponseDto.builder()
                .serviceTicketId(1L)
                .status("CREATED")
                .build();
        when(serviceTicketMapper.toResponseDto(ticket)).thenReturn(dto);

        Page<ServiceTicketResponseDto> result = service.getServiceTicketsByStatus(
                ServiceTicketStatus.CREATED, 0, 5);

        assertEquals(1, result.getTotalElements());
        assertSame(dto, result.getContent().get(0));
        verify(serviceTicketRepository).findByStatus(ServiceTicketStatus.CREATED, pageable);
    }

    @Test
    void getServiceTicketsByCreatedAt_ShouldReturnPagedDtos() {
        Pageable pageable = PageRequest.of(0, 5);
        LocalDateTime createdAt = LocalDateTime.now();
        ServiceTicket ticket = ServiceTicket.builder()
                .serviceTicketId(1L)
                .createdAt(createdAt)
                .build();
        Page<ServiceTicket> page = new PageImpl<>(List.of(ticket), pageable, 1);

        when(serviceTicketRepository.findByCreatedAt(createdAt, pageable)).thenReturn(page);

        ServiceTicketResponseDto dto = ServiceTicketResponseDto.builder()
                .serviceTicketId(1L)
                .build();
        when(serviceTicketMapper.toResponseDto(ticket)).thenReturn(dto);

        Page<ServiceTicketResponseDto> result = service.getServiceTicketsByCreatedAt(createdAt, pageable);

        assertEquals(1, result.getTotalElements());
        verify(serviceTicketRepository).findByCreatedAt(createdAt, pageable);
    }

    @Test
    void countServiceTicketByDate_ShouldReturnCount() {
        LocalDate date = LocalDate.now();
        when(serviceTicketRepository.countByDate(date)).thenReturn(5L);

        long result = service.countServiceTicketByDate(date);

        assertEquals(5L, result);
        verify(serviceTicketRepository).countByDate(date);
    }

    @Test
    void getCompletedTicketsByMonth_ShouldReturnListOfMaps() {
        Object[] row1 = new Object[]{2025, 1, 10L};
        Object[] row2 = new Object[]{2025, 2, 15L};
        when(serviceTicketRepository.countCompletedTicketsGroupedByMonth("COMPLETED"))
                .thenReturn(List.of(row1, row2));

        List<Map<String, Object>> result = service.getCompletedTicketsByMonth();

        assertEquals(2, result.size());
        assertEquals(2025, result.get(0).get("year"));
        assertEquals(1, result.get(0).get("month"));
        assertEquals(10L, result.get(0).get("count"));
        assertEquals(2025, result.get(1).get("year"));
        assertEquals(2, result.get(1).get("month"));
        assertEquals(15L, result.get(1).get("count"));
        verify(serviceTicketRepository).countCompletedTicketsGroupedByMonth("COMPLETED");
    }

    @Test
    void getTicketCountsByType_ShouldReturnListOfMaps() {
        Object[] row1 = new Object[]{"Bảo dưỡng", 5L};
        Object[] row2 = new Object[]{"Sửa chữa", 3L};
        when(serviceTicketRepository.countTicketsByTypeForMonth(2025, 1))
                .thenReturn(List.of(row1, row2));

        List<Map<String, Object>> result = service.getTicketCountsByType(2025, 1);

        assertEquals(2, result.size());
        assertEquals("Bảo dưỡng", result.get(0).get("type"));
        assertEquals(5L, result.get(0).get("count"));
        assertEquals("Sửa chữa", result.get(1).get("type"));
        assertEquals(3L, result.get(1).get("count"));
        verify(serviceTicketRepository).countTicketsByTypeForMonth(2025, 1);
    }
}

