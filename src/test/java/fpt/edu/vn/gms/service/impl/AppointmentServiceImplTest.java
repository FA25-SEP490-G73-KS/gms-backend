package fpt.edu.vn.gms.service.impl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;

import fpt.edu.vn.gms.base.BaseServiceTest;
import fpt.edu.vn.gms.common.enums.AppointmentStatus;
import fpt.edu.vn.gms.common.enums.CustomerLoyaltyLevel;
import fpt.edu.vn.gms.dto.request.AppointmentRequestDto;
import fpt.edu.vn.gms.dto.response.AppointmentBySlotResponse;
import fpt.edu.vn.gms.dto.response.AppointmentResponseDto;
import fpt.edu.vn.gms.dto.response.TimeSlotDto;
import fpt.edu.vn.gms.entity.*;
import fpt.edu.vn.gms.exception.ResourceNotFoundException;
import fpt.edu.vn.gms.mapper.AppointmentMapper;
import fpt.edu.vn.gms.repository.*;
import fpt.edu.vn.gms.service.CodeSequenceService;
import fpt.edu.vn.gms.service.zalo.ZnsNotificationService;

public class AppointmentServiceImplTest extends BaseServiceTest {

  @Mock
  private VehicleRepository vehicleRepo;

  @Mock
  private CustomerRepository customerRepo;

  @Mock
  private TimeSlotRepository timeSlotRepo;

  @Mock
  private AppointmentRepository appointmentRepo;

  @Mock
  private ServiceTypeRepository serviceTypeRepo;

  @Mock
  private DiscountPolicyRepository discountPolicyRepo;

  @Mock
  private CodeSequenceService codeSequenceService;

  @Mock
  private ZnsNotificationService znsNotificationService;

  @InjectMocks
  private AppointmentServiceImpl appointmentServiceImpl;

  @Test
  void getTimeSlotsByDate_WhenSlotsExist_ShouldReturnTimeSlotDtos() {
    LocalDate date = LocalDate.now();
    TimeSlot slot = TimeSlot.builder()
        .timeSlotId(1L)
        .label("Sáng")
        .startTime(LocalTime.of(8, 0))
        .endTime(LocalTime.of(10, 0))
        .maxCapacity(2)
        .build();
    when(timeSlotRepo.findAll()).thenReturn(List.of(slot));
    when(appointmentRepo.countByAppointmentDateAndTimeSlot(date, slot)).thenReturn(1);

    List<TimeSlotDto> result = appointmentServiceImpl.getTimeSlotsByDate(date);

    assertEquals(1, result.size());
    assertEquals(slot.getLabel(), result.get(0).getLabel());
    assertEquals(1, result.get(0).getBooked());
    assertTrue(result.get(0).isAvailable());
  }

  @Test
  void createAppointment_WhenCustomerIsNew_ShouldCreateCustomerAndAppointment() throws Exception {
    AppointmentRequestDto dto = AppointmentRequestDto.builder()
        .customerName("Alice")
        .phoneNumber("0123456789")
        .licensePlate("ABC123")
        .appointmentDate(LocalDate.now())
        .timeSlotIndex(1)
        .serviceType(List.of(1L))
        .note("Note")
        .build();

    DiscountPolicy policy = DiscountPolicy.builder().loyaltyLevel(CustomerLoyaltyLevel.BRONZE).build();
    when(discountPolicyRepo.findByLoyaltyLevel(CustomerLoyaltyLevel.BRONZE)).thenReturn(Optional.of(policy));
    when(customerRepo.findByPhone(dto.getPhoneNumber())).thenReturn(Optional.empty());
    when(appointmentRepo.countByCustomerAndAppointmentDate(any(), any())).thenReturn(0);

    Vehicle vehicle = Vehicle.builder().licensePlate(dto.getLicensePlate()).build();
    when(vehicleRepo.findByLicensePlate(dto.getLicensePlate())).thenReturn(Optional.empty());
    when(vehicleRepo.save(any(Vehicle.class))).thenReturn(vehicle);

    TimeSlot slot = TimeSlot.builder().timeSlotId(1L).label("Sáng").maxCapacity(2).build();
    when(timeSlotRepo.findAll()).thenReturn(List.of(slot));
    when(appointmentRepo.countByAppointmentDateAndTimeSlot(dto.getAppointmentDate(), slot)).thenReturn(0);

    ServiceType serviceType = ServiceType.builder().id(1L).build();
    when(serviceTypeRepo.getById(1L)).thenReturn(serviceType);

    when(codeSequenceService.generateCode("APT")).thenReturn("APT001");

    Appointment appointment = Appointment.builder()
        .appointmentId(1L)
        .appointmentCode("APT001")
        .customerName(dto.getCustomerName())
        .vehicle(vehicle)
        .timeSlot(slot)
        .appointmentDate(dto.getAppointmentDate())
        .serviceTypes(List.of(serviceType))
        .description(dto.getNote())
        .status(AppointmentStatus.CONFIRMED)
        .build();
    when(appointmentRepo.save(any(Appointment.class))).thenReturn(appointment);

    AppointmentResponseDto responseDto = AppointmentResponseDto.builder().appointmentCode("APT001").build();
    try (MockedStatic<AppointmentMapper> mapperMock = mockStatic(AppointmentMapper.class)) {
      mapperMock.when(() -> AppointmentMapper.toDto(any(Appointment.class))).thenReturn(responseDto);

      AppointmentResponseDto result = appointmentServiceImpl.createAppointment(dto);

      assertNotNull(result);
      assertEquals("APT001", result.getAppointmentCode());
      verify(customerRepo).save(any(Customer.class));
      verify(appointmentRepo).save(any(Appointment.class));
      verify(znsNotificationService).sendAppointmentConfirmation(any(Appointment.class));
    }
  }

  @Test
  void createAppointment_WhenCustomerInactive_ShouldCreateNewCustomer() {
    AppointmentRequestDto dto = AppointmentRequestDto.builder()
        .customerName("Bob")
        .phoneNumber("0987654321")
        .licensePlate("XYZ789")
        .appointmentDate(LocalDate.now())
        .timeSlotIndex(1)
        .serviceType(List.of(2L))
        .note("Note")
        .build();

    DiscountPolicy policy = DiscountPolicy.builder().loyaltyLevel(CustomerLoyaltyLevel.BRONZE).build();
    when(discountPolicyRepo.findByLoyaltyLevel(CustomerLoyaltyLevel.BRONZE)).thenReturn(Optional.of(policy));

    Customer oldCustomer = Customer.builder().isActive(false).build();
    when(customerRepo.findByPhone(dto.getPhoneNumber())).thenReturn(Optional.of(oldCustomer));
    when(appointmentRepo.countByCustomerAndAppointmentDate(any(), any())).thenReturn(0);

    Vehicle vehicle = Vehicle.builder().licensePlate(dto.getLicensePlate()).build();
    when(vehicleRepo.findByLicensePlate(dto.getLicensePlate())).thenReturn(Optional.empty());
    when(vehicleRepo.save(any(Vehicle.class))).thenReturn(vehicle);

    TimeSlot slot = TimeSlot.builder().timeSlotId(1L).label("Chiều").maxCapacity(2).build();
    when(timeSlotRepo.findAll()).thenReturn(List.of(slot));
    when(appointmentRepo.countByAppointmentDateAndTimeSlot(dto.getAppointmentDate(), slot)).thenReturn(0);

    ServiceType serviceType = ServiceType.builder().id(2L).build();
    when(serviceTypeRepo.getById(2L)).thenReturn(serviceType);

    when(codeSequenceService.generateCode("APT")).thenReturn("APT002");

    Appointment appointment = Appointment.builder()
        .appointmentId(2L)
        .appointmentCode("APT002")
        .customerName(dto.getCustomerName())
        .vehicle(vehicle)
        .timeSlot(slot)
        .appointmentDate(dto.getAppointmentDate())
        .serviceTypes(List.of(serviceType))
        .description(dto.getNote())
        .status(AppointmentStatus.CONFIRMED)
        .build();
    when(appointmentRepo.save(any(Appointment.class))).thenReturn(appointment);

    AppointmentResponseDto responseDto = AppointmentResponseDto.builder().appointmentCode("APT002").build();
    try (MockedStatic<AppointmentMapper> mapperMock = mockStatic(AppointmentMapper.class)) {
      mapperMock.when(() -> AppointmentMapper.toDto(any(Appointment.class))).thenReturn(responseDto);

      AppointmentResponseDto result = appointmentServiceImpl.createAppointment(dto);

      assertNotNull(result);
      assertEquals("APT002", result.getAppointmentCode());
      verify(customerRepo, times(1)).save(any(Customer.class));
    }
  }

  @Test
  void createAppointment_WhenMaxAppointmentsReached_ShouldThrowException() {
    AppointmentRequestDto dto = AppointmentRequestDto.builder()
        .customerName("Max")
        .phoneNumber("0123456789")
        .licensePlate("MAX123")
        .appointmentDate(LocalDate.now())
        .timeSlotIndex(1)
        .serviceType(List.of(1L))
        .note("Note")
        .build();

    DiscountPolicy policy = DiscountPolicy.builder().loyaltyLevel(CustomerLoyaltyLevel.BRONZE).build();
    Customer customer = Customer.builder().isActive(true).build();
    when(discountPolicyRepo.findByLoyaltyLevel(CustomerLoyaltyLevel.BRONZE)).thenReturn(Optional.of(policy));
    when(customerRepo.findByPhone(dto.getPhoneNumber())).thenReturn(Optional.of(customer));
    when(appointmentRepo.countByCustomerAndAppointmentDate(any(), any(LocalDate.class)))
        .thenReturn(1);

    Exception ex = assertThrows(IllegalArgumentException.class, () -> appointmentServiceImpl.createAppointment(dto));
    assertTrue(ex.getMessage().contains("Bạn chỉ được đặt tối đa"));
  }

  @Test
  void createAppointment_WhenTimeSlotIndexInvalid_ShouldThrowException() {
    AppointmentRequestDto dto = AppointmentRequestDto.builder()
        .customerName("Invalid")
        .phoneNumber("0123456789")
        .licensePlate("INV123")
        .appointmentDate(LocalDate.now())
        .timeSlotIndex(2)
        .serviceType(List.of(1L))
        .note("Note")
        .build();

    DiscountPolicy policy = DiscountPolicy.builder().loyaltyLevel(CustomerLoyaltyLevel.BRONZE).build();
    Customer customer = Customer.builder().isActive(true).build();
    when(discountPolicyRepo.findByLoyaltyLevel(CustomerLoyaltyLevel.BRONZE)).thenReturn(Optional.of(policy));
    when(customerRepo.findByPhone(dto.getPhoneNumber())).thenReturn(Optional.of(customer));
    when(appointmentRepo.countByCustomerAndAppointmentDate(any(), eq(dto.getAppointmentDate()))).thenReturn(0);

    Vehicle vehicle = Vehicle.builder().licensePlate(dto.getLicensePlate()).build();
    when(vehicleRepo.findByLicensePlate(dto.getLicensePlate())).thenReturn(Optional.of(vehicle));

    when(timeSlotRepo.findAll()).thenReturn(List.of(TimeSlot.builder().timeSlotId(1L).build()));

    Exception ex = assertThrows(IllegalArgumentException.class, () -> appointmentServiceImpl.createAppointment(dto));
    assertTrue(ex.getMessage().contains("Khung giờ không tồn tại"));
  }

  @Test
  void createAppointment_WhenTimeSlotFull_ShouldThrowException() {
    AppointmentRequestDto dto = AppointmentRequestDto.builder()
        .customerName("Full")
        .phoneNumber("0123456789")
        .licensePlate("FULL123")
        .appointmentDate(LocalDate.now())
        .timeSlotIndex(1)
        .serviceType(List.of(1L))
        .note("Note")
        .build();

    DiscountPolicy policy = DiscountPolicy.builder().loyaltyLevel(CustomerLoyaltyLevel.BRONZE).build();
    Customer customer = Customer.builder().isActive(true).build();
    when(discountPolicyRepo.findByLoyaltyLevel(CustomerLoyaltyLevel.BRONZE)).thenReturn(Optional.of(policy));
    when(customerRepo.findByPhone(dto.getPhoneNumber())).thenReturn(Optional.of(customer));
    when(appointmentRepo.countByCustomerAndAppointmentDate(any(), eq(dto.getAppointmentDate()))).thenReturn(0);

    Vehicle vehicle = Vehicle.builder().licensePlate(dto.getLicensePlate()).build();
    when(vehicleRepo.findByLicensePlate(dto.getLicensePlate())).thenReturn(Optional.of(vehicle));

    TimeSlot slot = TimeSlot.builder().timeSlotId(1L).maxCapacity(1).build();
    when(timeSlotRepo.findAll()).thenReturn(List.of(slot));
    when(appointmentRepo.countByAppointmentDateAndTimeSlot(dto.getAppointmentDate(), slot)).thenReturn(1);

    Exception ex = assertThrows(IllegalArgumentException.class, () -> appointmentServiceImpl.createAppointment(dto));
    assertTrue(ex.getMessage().contains("Khung giờ bạn đặt đã đầy"));
  }

  @Test
  void getAllAppointments_WhenCalled_ShouldReturnPagedResult() {
    Appointment appointment = Appointment.builder().appointmentId(1L).build();
    Page<Appointment> page = new PageImpl<>(List.of(appointment));
    when(appointmentRepo.findAll(any(Pageable.class))).thenReturn(page);

    AppointmentResponseDto dto = AppointmentResponseDto.builder().appointmentCode("APT001").build();
    try (MockedStatic<AppointmentMapper> mapperMock = mockStatic(AppointmentMapper.class)) {
      mapperMock.when(() -> AppointmentMapper.toDto(any(Appointment.class))).thenReturn(dto);

      Page<AppointmentResponseDto> result = appointmentServiceImpl.getAllAppointments(0, 10);

      assertEquals(1, result.getTotalElements());
      assertEquals("APT001", result.getContent().get(0).getAppointmentCode());
    }
  }

  @Test
  void getAppointmentsByStatus_WhenCalled_ShouldReturnPagedResult() {
    Appointment appointment = Appointment.builder().appointmentId(2L).status(AppointmentStatus.CONFIRMED).build();
    Page<Appointment> page = new PageImpl<>(List.of(appointment));
    Pageable pageable = PageRequest.of(0, 10);
    when(appointmentRepo.getByStatus(AppointmentStatus.CONFIRMED, pageable)).thenReturn(page);

    AppointmentResponseDto dto = AppointmentResponseDto.builder().appointmentCode("APT002").build();
    try (MockedStatic<AppointmentMapper> mapperMock = mockStatic(AppointmentMapper.class)) {
      mapperMock.when(() -> AppointmentMapper.toDto(any(Appointment.class))).thenReturn(dto);

      Page<AppointmentResponseDto> result = appointmentServiceImpl.getAppointmentsByStatus(AppointmentStatus.CONFIRMED,
          pageable);

      assertEquals(1, result.getTotalElements());
      assertEquals("APT002", result.getContent().get(0).getAppointmentCode());
    }
  }

  @Test
  void getAppointmentsByDate_WhenCalled_ShouldReturnGroupedBySlot() {
    TimeSlot slot = TimeSlot.builder().label("Sáng").startTime(LocalTime.of(8, 0)).endTime(LocalTime.of(10, 0)).build();
    Appointment appointment = Appointment.builder().appointmentId(1L).timeSlot(slot).build();
    when(appointmentRepo.findByAppointmentDate(any(LocalDate.class))).thenReturn(List.of(appointment));

    List<AppointmentBySlotResponse> result = appointmentServiceImpl.getAppointmentsByDate(LocalDate.now());

    assertEquals(1, result.size());
    assertEquals("Sáng", result.get(0).getLabel());
  }

  @Test
  void getAppointmentById_WhenAppointmentExists_ShouldReturnDto() {
    Appointment appointment = Appointment.builder().appointmentId(1L).build();
    when(appointmentRepo.findById(1L)).thenReturn(Optional.of(appointment));

    AppointmentResponseDto dto = AppointmentResponseDto.builder().appointmentCode("APT001").build();
    try (MockedStatic<AppointmentMapper> mapperMock = mockStatic(AppointmentMapper.class)) {
      mapperMock.when(() -> AppointmentMapper.toDto(appointment)).thenReturn(dto);

      AppointmentResponseDto result = appointmentServiceImpl.getAppointmentById(1L);

      assertEquals("APT001", result.getAppointmentCode());
    }
  }

  @Test
  void getAppointmentById_WhenNotFound_ShouldThrowException() {
    when(appointmentRepo.findById(99L)).thenReturn(Optional.empty());
    assertThrows(RuntimeException.class, () -> appointmentServiceImpl.getAppointmentById(99L));
  }

  @Test
  void updateArrivedStatus_WhenAppointmentExists_ShouldUpdateStatusAndReturnDto() {
    Appointment appointment = Appointment.builder().appointmentId(1L).status(AppointmentStatus.CONFIRMED).build();
    when(appointmentRepo.findById(1L)).thenReturn(Optional.of(appointment));
    when(appointmentRepo.save(any(Appointment.class))).thenReturn(appointment);

    AppointmentResponseDto dto = AppointmentResponseDto.builder().appointmentCode("APT001").build();
    try (MockedStatic<AppointmentMapper> mapperMock = mockStatic(AppointmentMapper.class)) {
      mapperMock.when(() -> AppointmentMapper.toDto(appointment)).thenReturn(dto);

      AppointmentResponseDto result = appointmentServiceImpl.updateArrivedStatus(1L);

      assertEquals("APT001", result.getAppointmentCode());
      assertEquals(AppointmentStatus.ARRIVED, appointment.getStatus());
    }
  }

  @Test
  void updateArrivedStatus_WhenNotFound_ShouldThrowException() {
    when(appointmentRepo.findById(99L)).thenReturn(Optional.empty());
    assertThrows(RuntimeException.class, () -> appointmentServiceImpl.updateArrivedStatus(99L));
  }

  @Test
  void countAppointmentsByDate_WhenCalled_ShouldReturnCount() {
    when(appointmentRepo.countByDate(any(LocalDate.class))).thenReturn(5L);
    long count = appointmentServiceImpl.countAppointmentsByDate(LocalDate.now());
    assertEquals(5L, count);
  }

  @Test
  void updateStatus_WhenAppointmentExists_ShouldUpdateStatusAndReturnDto() {
    Appointment appointment = Appointment.builder().appointmentId(1L).status(AppointmentStatus.CONFIRMED).build();
    when(appointmentRepo.findById(1L)).thenReturn(Optional.of(appointment));
    when(appointmentRepo.save(any(Appointment.class))).thenReturn(appointment);

    AppointmentResponseDto dto = AppointmentResponseDto.builder().appointmentCode("APT001").build();
    try (MockedStatic<AppointmentMapper> mapperMock = mockStatic(AppointmentMapper.class)) {
      mapperMock.when(() -> AppointmentMapper.toDto(appointment)).thenReturn(dto);

      AppointmentResponseDto result = appointmentServiceImpl.updateStatus(1L, AppointmentStatus.CANCELLED);

      assertEquals("APT001", result.getAppointmentCode());
      assertEquals(AppointmentStatus.CANCELLED, appointment.getStatus());
    }
  }

  @Test
  void updateStatus_WhenAppointmentCancelled_ShouldThrowException() {
    Appointment appointment = Appointment.builder().appointmentId(1L).status(AppointmentStatus.CANCELLED).build();
    when(appointmentRepo.findById(1L)).thenReturn(Optional.of(appointment));
    assertThrows(RuntimeException.class, () -> appointmentServiceImpl.updateStatus(1L, AppointmentStatus.CONFIRMED));
  }

  @Test
  void updateStatus_WhenNotFound_ShouldThrowResourceNotFoundException() {
    when(appointmentRepo.findById(99L)).thenReturn(Optional.empty());
    assertThrows(ResourceNotFoundException.class,
        () -> appointmentServiceImpl.updateStatus(99L, AppointmentStatus.CONFIRMED));
  }

  @Test
  void confirmAppointment_WhenPendingAndNotConfirmed_ShouldSetConfirmedAtAndReturnDto() {
    Appointment appointment = Appointment.builder()
        .appointmentId(1L)
        .status(AppointmentStatus.PENDING)
        .confirmedAt(null)
        .build();
    when(appointmentRepo.findById(1L)).thenReturn(Optional.of(appointment));
    when(appointmentRepo.save(any(Appointment.class))).thenReturn(appointment);

    AppointmentResponseDto dto = AppointmentResponseDto.builder().appointmentCode("APT001").build();
    try (MockedStatic<AppointmentMapper> mapperMock = mockStatic(AppointmentMapper.class)) {
      mapperMock.when(() -> AppointmentMapper.toDto(appointment)).thenReturn(dto);

      AppointmentResponseDto result = appointmentServiceImpl.confirmAppointment(1L);

      assertEquals("APT001", result.getAppointmentCode());
      assertNotNull(appointment.getConfirmedAt());
    }
  }

  @Test
  void confirmAppointment_WhenNotPending_ShouldThrowException() {
    Appointment appointment = Appointment.builder()
        .appointmentId(1L)
        .status(AppointmentStatus.CONFIRMED)
        .confirmedAt(null)
        .build();
    when(appointmentRepo.findById(1L)).thenReturn(Optional.of(appointment));
    assertThrows(RuntimeException.class, () -> appointmentServiceImpl.confirmAppointment(1L));
  }

  @Test
  void confirmAppointment_WhenNotFound_ShouldThrowResourceNotFoundException() {
    when(appointmentRepo.findById(99L)).thenReturn(Optional.empty());
    assertThrows(ResourceNotFoundException.class, () -> appointmentServiceImpl.confirmAppointment(99L));
  }
}
