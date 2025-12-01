package fpt.edu.vn.gms.service.impl;

import fpt.edu.vn.gms.common.enums.AppointmentStatus;
import fpt.edu.vn.gms.common.enums.CustomerLoyaltyLevel;
import fpt.edu.vn.gms.dto.request.AppointmentRequestDto;
import fpt.edu.vn.gms.dto.response.AppointmentBySlotResponse;
import fpt.edu.vn.gms.dto.response.AppointmentResponseDto;
import fpt.edu.vn.gms.dto.response.TimeSlotDto;
import fpt.edu.vn.gms.entity.*;
import fpt.edu.vn.gms.exception.ResourceNotFoundException;
import fpt.edu.vn.gms.repository.*;
import fpt.edu.vn.gms.service.CodeSequenceService;
import fpt.edu.vn.gms.service.zalo.ZnsNotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceImplTest {

    @Mock
    VehicleRepository vehicleRepo;
    @Mock
    CustomerRepository customerRepo;
    @Mock
    TimeSlotRepository timeSlotRepo;
    @Mock
    AppointmentRepository appointmentRepo;
    @Mock
    ServiceTypeRepository serviceTypeRepo;
    @Mock
    DiscountPolicyRepository discountPolicyRepo;
    @Mock
    CodeSequenceService codeSequenceService;
    @Mock
    ZnsNotificationService znsNotificationService;

    @InjectMocks
    AppointmentServiceImpl appointmentService;

    private LocalDate today;
    private DiscountPolicy defaultPolicy;
    private Customer existingCustomer;
    private TimeSlot timeSlot;

    @BeforeEach
    void setUp() {
        today = LocalDate.now();

        defaultPolicy = DiscountPolicy.builder()
                .discountPolicyId(1L)
                .loyaltyLevel(CustomerLoyaltyLevel.BRONZE)
                .build();

        existingCustomer = Customer.builder()
                .customerId(10L)
                .fullName("Nguyen Van A")
                .phone("0909000000")
                .isActive(true)
                .discountPolicy(defaultPolicy)
                .build();

        timeSlot = TimeSlot.builder()
                .timeSlotId(1L)
                .label("08:00-10:00")
                .startTime(LocalTime.of(8, 0))
                .endTime(LocalTime.of(10, 0))
                .maxCapacity(3)
                .build();
    }

    private AppointmentRequestDto buildRequest() {
        return AppointmentRequestDto.builder()
                .customerName("Nguyen Van A")
                .phoneNumber("0909000000")
                .licensePlate("30A-123.45")
                .appointmentDate(today)
                .timeSlotIndex(1)
                .serviceType(List.of(1L, 2L))
                .note("Test note")
                .build();
    }

    @Test
    void getTimeSlotsByDate_ShouldReturnMappedDtos() {
        when(timeSlotRepo.findAll()).thenReturn(List.of(timeSlot));
        when(appointmentRepo.countByAppointmentDateAndTimeSlot(today, timeSlot)).thenReturn(1);

        List<TimeSlotDto> result = appointmentService.getTimeSlotsByDate(today);

        assertEquals(1, result.size());
        TimeSlotDto dto = result.get(0);
        assertEquals(timeSlot.getTimeSlotId(), dto.getTimeSlotId());
        assertEquals(timeSlot.getLabel(), dto.getLabel());
        assertEquals(1, dto.getBooked());
        assertEquals(timeSlot.getMaxCapacity(), dto.getMaxCapacity());
        assertTrue(dto.isAvailable());

        verify(timeSlotRepo).findAll();
        verify(appointmentRepo).countByAppointmentDateAndTimeSlot(today, timeSlot);
    }

    @Test
    void createAppointment_ShouldCreateNewCustomerAndVehicle_WhenCustomerNotExist() throws Exception {
        AppointmentRequestDto dto = buildRequest();

        when(discountPolicyRepo.findByLoyaltyLevel(CustomerLoyaltyLevel.BRONZE))
                .thenReturn(Optional.of(defaultPolicy));
        when(customerRepo.findByPhone(anyString())).thenReturn(Optional.empty());
        when(customerRepo.save(any(Customer.class))).thenAnswer(invocation -> {
            Customer c = invocation.getArgument(0);
            c.setCustomerId(10L);
            return c;
        });
        when(appointmentRepo.countByCustomerAndAppointmentDate(any(Customer.class), eq(today)))
                .thenReturn(0);
        when(vehicleRepo.findByLicensePlate(dto.getLicensePlate()))
                .thenReturn(Optional.empty());
        when(vehicleRepo.save(any(Vehicle.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(timeSlotRepo.findAll()).thenReturn(List.of(timeSlot));
        when(appointmentRepo.countByAppointmentDateAndTimeSlot(today, timeSlot))
                .thenReturn(0);

        ServiceType type1 = ServiceType.builder().id(1L).name("T1").build();
        ServiceType type2 = ServiceType.builder().id(2L).name("T2").build();
        when(serviceTypeRepo.getById(1L)).thenReturn(type1);
        when(serviceTypeRepo.getById(2L)).thenReturn(type2);

        when(codeSequenceService.generateCode("APT")).thenReturn("APT001");

        Appointment saved = Appointment.builder()
                .appointmentId(100L)
                .appointmentCode("APT001")
                .customer(existingCustomer)
                .vehicle(Vehicle.builder().vehicleId(1L).licensePlate(dto.getLicensePlate()).build())
                .timeSlot(timeSlot)
                .appointmentDate(today)
                .serviceTypes(List.of(type1, type2))
                .status(AppointmentStatus.CONFIRMED)
                .build();
        when(appointmentRepo.save(any(Appointment.class))).thenReturn(saved);

        doNothing().when(znsNotificationService).sendAppointmentConfirmation(saved);

        AppointmentResponseDto response = appointmentService.createAppointment(dto);

        assertNotNull(response);
        assertEquals(saved.getAppointmentId(), response.getAppointmentId());
        assertEquals(saved.getVehicle().getLicensePlate(), response.getLicensePlate());
        assertEquals(saved.getStatus(), response.getStatus());

        verify(discountPolicyRepo).findByLoyaltyLevel(CustomerLoyaltyLevel.BRONZE);
        verify(customerRepo, times(2)).save(any(Customer.class));
        verify(vehicleRepo).save(any(Vehicle.class));
        verify(appointmentRepo).save(any(Appointment.class));
        verify(znsNotificationService).sendAppointmentConfirmation(saved);
    }

    @Test
    void createAppointment_ShouldThrow_WhenDailyLimitExceeded() {
        AppointmentRequestDto dto = buildRequest();

        when(discountPolicyRepo.findByLoyaltyLevel(CustomerLoyaltyLevel.BRONZE))
                .thenReturn(Optional.of(defaultPolicy));
        when(customerRepo.findByPhone(anyString())).thenReturn(Optional.of(existingCustomer));
        when(appointmentRepo.countByCustomerAndAppointmentDate(existingCustomer, today))
                .thenReturn(1); // MAX_APPOINTMENTS_PER_DAY = 1

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> appointmentService.createAppointment(dto));
        assertTrue(ex.getMessage().contains("Bạn chỉ được đặt tối đa"));

        verify(customerRepo).findByPhone(anyString());
        verify(appointmentRepo).countByCustomerAndAppointmentDate(existingCustomer, today);
        verifyNoInteractions(vehicleRepo);
    }

    @Test
    void createAppointment_ShouldThrow_WhenTimeSlotIndexInvalid() {
        AppointmentRequestDto dto = buildRequest();
        dto.setTimeSlotIndex(5); // out of range

        when(discountPolicyRepo.findByLoyaltyLevel(CustomerLoyaltyLevel.BRONZE))
                .thenReturn(Optional.of(defaultPolicy));
        when(customerRepo.findByPhone(anyString())).thenReturn(Optional.of(existingCustomer));
        when(appointmentRepo.countByCustomerAndAppointmentDate(existingCustomer, today))
                .thenReturn(0);

        when(vehicleRepo.findByLicensePlate(dto.getLicensePlate()))
                .thenReturn(Optional.of(Vehicle.builder()
                        .vehicleId(1L)
                        .licensePlate(dto.getLicensePlate())
                        .customer(existingCustomer)
                        .build()));

        when(timeSlotRepo.findAll()).thenReturn(List.of(timeSlot));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> appointmentService.createAppointment(dto));
        assertTrue(ex.getMessage().contains("Khung giờ không tồn tại"));

        verify(timeSlotRepo).findAll();
        verify(appointmentRepo, never()).countByAppointmentDateAndTimeSlot(any(), any());
    }

    @Test
    void getAllAppointments_ShouldReturnMappedPage() {
        Appointment appointment = Appointment.builder()
                .appointmentId(1L)
                .customer(existingCustomer)
                .vehicle(Vehicle.builder().vehicleId(1L).licensePlate("30A-123.45").build())
                .timeSlot(timeSlot)
                .appointmentDate(today)
                .serviceTypes(Collections.emptyList())
                .status(AppointmentStatus.CONFIRMED)
                .build();

        Page<Appointment> page = new PageImpl<>(List.of(appointment));
        Pageable pageable = PageRequest.of(0, 10, Sort.by("appointmentDate").descending());

        when(appointmentRepo.findAll(pageable)).thenReturn(page);

        Page<AppointmentResponseDto> result = appointmentService.getAllAppointments(0, 10);

        assertEquals(1, result.getTotalElements());
        assertEquals(appointment.getAppointmentId(), result.getContent().get(0).getAppointmentId());

        verify(appointmentRepo).findAll(pageable);
    }

    @Test
    void getAppointmentsByStatus_ShouldDelegateToRepository() {
        Appointment appointment = Appointment.builder()
                .appointmentId(2L)
                .status(AppointmentStatus.CONFIRMED)
                .build();

        Pageable pageable = PageRequest.of(0, 5);
        Page<Appointment> page = new PageImpl<>(List.of(appointment), pageable, 1);

        when(appointmentRepo.getByStatus(AppointmentStatus.CONFIRMED, pageable))
                .thenReturn(page);

        Page<AppointmentResponseDto> result =
                appointmentService.getAppointmentsByStatus(AppointmentStatus.CONFIRMED, pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals(AppointmentStatus.CONFIRMED, result.getContent().get(0).getStatus());

        verify(appointmentRepo).getByStatus(AppointmentStatus.CONFIRMED, pageable);
    }

    @Test
    void getAppointmentsByDate_ShouldGroupAndSortByTime() {
        TimeSlot slot1 = TimeSlot.builder()
                .timeSlotId(1L)
                .label("08:00-09:00")
                .startTime(LocalTime.of(8, 0))
                .endTime(LocalTime.of(9, 0))
                .build();
        TimeSlot slot2 = TimeSlot.builder()
                .timeSlotId(2L)
                .label("09:00-10:00")
                .startTime(LocalTime.of(9, 0))
                .endTime(LocalTime.of(10, 0))
                .build();

        Appointment a1 = Appointment.builder()
                .appointmentId(1L)
                .timeSlot(slot2)
                .appointmentDate(today)
                .build();
        Appointment a2 = Appointment.builder()
                .appointmentId(2L)
                .timeSlot(slot1)
                .appointmentDate(today)
                .build();

        when(appointmentRepo.findByAppointmentDate(today))
                .thenReturn(List.of(a1, a2));

        List<AppointmentBySlotResponse> result = appointmentService.getAppointmentsByDate(today);

        assertEquals(2, result.size());
        // sorted by start time
        assertEquals(slot1.getStartTime(), result.get(0).getStartTime());
        assertEquals(slot2.getStartTime(), result.get(1).getStartTime());

        verify(appointmentRepo).findByAppointmentDate(today);
    }

    @Test
    void getAppointmentById_ShouldReturnDto_WhenFound() {
        Appointment appointment = Appointment.builder()
                .appointmentId(5L)
                .customer(existingCustomer)
                .vehicle(Vehicle.builder().vehicleId(1L).licensePlate("30A-123.45").build())
                .timeSlot(timeSlot)
                .appointmentDate(today)
                .serviceTypes(Collections.emptyList())
                .status(AppointmentStatus.PENDING)
                .build();

        when(appointmentRepo.findById(5L)).thenReturn(Optional.of(appointment));

        AppointmentResponseDto dto = appointmentService.getAppointmentById(5L);

        assertNotNull(dto);
        assertEquals(appointment.getAppointmentId(), dto.getAppointmentId());

        verify(appointmentRepo).findById(5L);
    }

    @Test
    void getAppointmentById_ShouldThrow_WhenNotFound() {
        when(appointmentRepo.findById(999L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> appointmentService.getAppointmentById(999L));
        assertTrue(ex.getMessage().contains("Appointment not found"));

        verify(appointmentRepo).findById(999L);
    }

    @Test
    void updateArrivedStatus_ShouldUpdateStatusToArrived() {
        Appointment appointment = Appointment.builder()
                .appointmentId(7L)
                .status(AppointmentStatus.PENDING)
                .build();

        when(appointmentRepo.findById(7L)).thenReturn(Optional.of(appointment));

        AppointmentResponseDto dto = appointmentService.updateArrivedStatus(7L);

        assertEquals(AppointmentStatus.ARRIVED, appointment.getStatus());
        assertEquals(AppointmentStatus.ARRIVED, dto.getStatus());

        verify(appointmentRepo).findById(7L);
        verify(appointmentRepo).save(appointment);
    }

    @Test
    void countAppointmentsByDate_ShouldDelegateToRepository() {
        when(appointmentRepo.countByDate(today)).thenReturn(5L);

        long count = appointmentService.countAppointmentsByDate(today);

        assertEquals(5L, count);
        verify(appointmentRepo).countByDate(today);
    }

    @Test
    void updateStatus_ShouldUpdateAndReturnDto_WhenValid() {
        Appointment appointment = Appointment.builder()
                .appointmentId(11L)
                .status(AppointmentStatus.PENDING)
                .build();

        when(appointmentRepo.findById(11L)).thenReturn(Optional.of(appointment));

        AppointmentResponseDto dto =
                appointmentService.updateStatus(11L, AppointmentStatus.CONFIRMED);

        assertEquals(AppointmentStatus.CONFIRMED, appointment.getStatus());
        assertEquals(AppointmentStatus.CONFIRMED, dto.getStatus());

        verify(appointmentRepo).findById(11L);
        verify(appointmentRepo).save(appointment);
    }

    @Test
    void updateStatus_ShouldThrow_WhenAppointmentCancelled() {
        Appointment appointment = Appointment.builder()
                .appointmentId(12L)
                .status(AppointmentStatus.CANCELLED)
                .build();

        when(appointmentRepo.findById(12L)).thenReturn(Optional.of(appointment));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> appointmentService.updateStatus(12L, AppointmentStatus.CONFIRMED));
        assertTrue(ex.getMessage().contains("Cannot change status of a cancelled appointment"));

        verify(appointmentRepo).findById(12L);
        verify(appointmentRepo, never()).save(any());
    }

    @Test
    void updateStatus_ShouldThrowResourceNotFound_WhenIdNotExists() {
        when(appointmentRepo.findById(404L)).thenReturn(Optional.empty());

        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class,
                () -> appointmentService.updateStatus(404L, AppointmentStatus.CONFIRMED));
        assertTrue(ex.getMessage().contains("Appointment not found with id: 404"));

        verify(appointmentRepo).findById(404L);
    }

    @Test
    void confirmAppointment_ShouldSetConfirmedAt_WhenPending() {
        Appointment appointment = Appointment.builder()
                .appointmentId(20L)
                .status(AppointmentStatus.PENDING)
                .build();

        when(appointmentRepo.findById(20L)).thenReturn(Optional.of(appointment));

        AppointmentResponseDto dto = appointmentService.confirmAppointment(20L);

        assertNotNull(appointment.getConfirmedAt());
        assertEquals(AppointmentStatus.PENDING, dto.getStatus()); // status not changed inside method

        verify(appointmentRepo).findById(20L);
        verify(appointmentRepo).save(appointment);
    }

    @Test
    void confirmAppointment_ShouldThrow_WhenStatusNotPending() {
        Appointment appointment = Appointment.builder()
                .appointmentId(21L)
                .status(AppointmentStatus.CONFIRMED)
                .build();

        when(appointmentRepo.findById(21L)).thenReturn(Optional.of(appointment));

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> appointmentService.confirmAppointment(21L));
        assertTrue(ex.getMessage().contains("Chỉ có thể xác nhận những lịch hẹn đang chờ."));

        verify(appointmentRepo).findById(21L);
        verify(appointmentRepo, never()).save(any());
    }

    @Test
    void confirmAppointment_ShouldNotOverrideConfirmedAt_WhenAlreadySet() {
        Appointment appointment = Appointment.builder()
                .appointmentId(22L)
                .status(AppointmentStatus.PENDING)
                .confirmedAt(java.time.LocalDateTime.now())
                .build();

        when(appointmentRepo.findById(22L)).thenReturn(Optional.of(appointment));

        java.time.LocalDateTime before = appointment.getConfirmedAt();
        AppointmentResponseDto dto = appointmentService.confirmAppointment(22L);

        assertEquals(before, appointment.getConfirmedAt());
        assertEquals(AppointmentStatus.PENDING, dto.getStatus());

        verify(appointmentRepo).findById(22L);
        verify(appointmentRepo, never()).save(any());
    }
}