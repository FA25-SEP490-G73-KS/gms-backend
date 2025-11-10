package fpt.edu.vn.gms.service.impl;

import fpt.edu.vn.gms.common.AppointmentStatus;
import fpt.edu.vn.gms.common.CustomerLoyaltyLevel;
import fpt.edu.vn.gms.common.ServiceTicketStatus;
import fpt.edu.vn.gms.dto.request.AppointmentRequestDto;
import fpt.edu.vn.gms.dto.response.AppointmentResponseDto;
import fpt.edu.vn.gms.dto.response.TimeSlotDto;
import fpt.edu.vn.gms.entity.*;
import fpt.edu.vn.gms.mapper.AppointmentMapper;
import fpt.edu.vn.gms.repository.*;
import fpt.edu.vn.gms.service.AppointmentService;
import fpt.edu.vn.gms.service.CodeSequenceService;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {

    private final VehicleRepository vehicleRepo;
    private final CustomerRepository customerRepo;
    private final TimeSlotRepository timeSlotRepo;
    private final AppointmentRepository appointmentRepo;
    private final ServiceTicketRepository serviceTicketRepo;
    private final ServiceTypeRepository serviceTypeRepo;
    private final EmployeeRepository employeeRepo;
    private final CodeSequenceService codeSequenceService;

    private static final int MAX_APPOINTMENTS_PER_DAY = 1;

    public List<TimeSlotDto> getTimeSlotsByDate(LocalDate date) {
        List<TimeSlot> slots = timeSlotRepo.findAll();
        return slots.stream().map(slot -> {
            int booked = appointmentRepo.countByAppointmentDateAndTimeSlot(date, slot);
            return TimeSlotDto.builder()
                    .timeSlotId(slot.getTimeSlotId())
                    .label(slot.getLabel())
                    .startTime(slot.getStartTime())
                    .endTime(slot.getEndTime())
                    .booked(booked)
                    .maxCapacity(slot.getMaxCapacity())
                    .available(booked < slot.getMaxCapacity())
                    .build();
        }).collect(Collectors.toList());
    }

    public AppointmentResponseDto createAppointment(AppointmentRequestDto dto) {

        // Check customer theo số điện thoại
        Customer customer = customerRepo.findByPhone(dto.getPhoneNumber())
                .orElseGet(() -> {
                    // Nếu chưa có thì tạo mới customer
                    Customer newCustomer = Customer.builder()
                            .phone(dto.getPhoneNumber())
                            .loyaltyLevel(CustomerLoyaltyLevel.BRONZE)
                            .build();
                    return customerRepo.save(newCustomer);
                });

        // Giới hạn số lần đặt lịch theo ngày
        int countToday = appointmentRepo.countByCustomerAndAppointmentDate(customer, dto.getAppointmentDate());
        if (countToday >= MAX_APPOINTMENTS_PER_DAY) {
            throw new IllegalArgumentException("Bạn chỉ được đặt tối đa " + MAX_APPOINTMENTS_PER_DAY + " lịch trong ngày " + dto.getAppointmentDate());
        }

        // Check vehicle theo license plate
        Vehicle vehicle = vehicleRepo.findByLicensePlate(dto.getLicensePlate())
                .orElseGet(() -> {
                    // Nếu xe chưa tồn tại thì tạo mới, gán customer vào
                    Vehicle newVehicle = Vehicle.builder()
                            .licensePlate(dto.getLicensePlate())
                            .customer(customer) // gán đúng customer đã tìm
                            .build();
                    return vehicleRepo.save(newVehicle);
                });

        // Find slot by index
        List<TimeSlot> allSlots = timeSlotRepo.findAll();
        if (dto.getTimeSlotIndex() < 1 || dto.getTimeSlotIndex() > allSlots.size()) {
            throw new IllegalArgumentException("Time slot index invalid");
        }
        TimeSlot slot = allSlots.get(dto.getTimeSlotIndex() - 1);

        // Check availability
        int booked = appointmentRepo.countByAppointmentDateAndTimeSlot(dto.getAppointmentDate(), slot);
        if (booked >= slot.getMaxCapacity()) {
            throw new IllegalArgumentException("Time slot is full");
        }

        // --- Lấy danh sách ServiceType từ danh sách ID ---
        List<ServiceType> serviceTypes = dto.getServiceType().stream()
                .map(serviceTypeRepo::getById) // có thể dùng findById nếu muốn kiểm tra tồn tại
                .toList();

        Appointment appointment = Appointment.builder()
                .appointmentCode(codeSequenceService.generateCode("APT"))
                .customer(vehicle.getCustomer())
                .customerName(dto.getCustomerName())
                .vehicle(vehicle)
                .timeSlot(slot)
                .appointmentDate(dto.getAppointmentDate())
                .serviceTypes(serviceTypes)
                .description(dto.getNote())
                .status(AppointmentStatus.CONFIRMED)
                .build();

        Appointment saved = appointmentRepo.save(appointment);

        return AppointmentMapper.toDto(saved);
    }

    @Override
    public Page<AppointmentResponseDto> getAllAppointments(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("appointmentDate").descending());
        return appointmentRepo.findAll(pageable)
                .map(AppointmentMapper::toDto);
    }

    @Override
    public Page<AppointmentResponseDto> getAppByDate(LocalDate date, int page, int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return appointmentRepo.findByAppointmentDate(date, pageable)
                .map(AppointmentMapper::toDto);
    }

    @Override
    public AppointmentResponseDto getAppointmentById(Long id) {
        Appointment appointment = appointmentRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found with id: " + id));


        return AppointmentMapper.toDto(appointment);
    }

    @Override
    public AppointmentResponseDto updateStatus(Long id) {
        Appointment appointment = appointmentRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found with id: " + id));

        // Tránh tạo phiếu dịch vụ 2 lần
        if (AppointmentStatus.ARRIVED.equals(appointment.getStatus())) {
            throw new RuntimeException("Appointment is arrived");
        }

        appointment.setStatus(AppointmentStatus.ARRIVED);
        appointmentRepo.save(appointment);

        // 2. Tạo phiếu dịch vụ
        createServiceTicketFromAppointment(id);

        return AppointmentMapper.toDto(appointment);
    }

    public void createServiceTicketFromAppointment(Long appId) {

        Appointment appointment = appointmentRepo.findById(appId)
                .orElseThrow(() -> new RuntimeException("Appointment not found with id: " + appId));

        // Lấy customer và vehicle từ appointment (nếu có)
        Customer customer = appointment.getCustomer();
        Vehicle vehicle = appointment.getVehicle();

        // Lấy thông tin cố vấn tạo
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String phoneNumber = authentication.getName();
        Employee employee = employeeRepo.findByPhone(phoneNumber);

        ServiceTicket newServiceTicket = ServiceTicket.builder()
                .serviceTicketCode(codeSequenceService.generateCode("STK"))
                .appointment(appointment)
                .customer(customer)
                .vehicle(vehicle)
                .serviceTypes(appointment.getServiceTypes())
                .status(ServiceTicketStatus.CREATED)
                .createdAt(LocalDateTime.now())
                .createdBy(employee)
                .build();

        // Lưu phiếu
        serviceTicketRepo.save(newServiceTicket);
    }
}
