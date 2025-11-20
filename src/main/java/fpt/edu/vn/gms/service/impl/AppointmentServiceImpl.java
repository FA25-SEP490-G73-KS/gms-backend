package fpt.edu.vn.gms.service.impl;

import fpt.edu.vn.gms.common.enums.AppointmentStatus;
import fpt.edu.vn.gms.common.enums.CustomerLoyaltyLevel;
import fpt.edu.vn.gms.common.enums.ServiceTicketStatus;
import fpt.edu.vn.gms.dto.request.AppointmentRequestDto;
import fpt.edu.vn.gms.dto.response.AppointmentBySlotResponse;
import fpt.edu.vn.gms.dto.response.AppointmentResponseDto;
import fpt.edu.vn.gms.dto.response.TimeSlotDto;
import fpt.edu.vn.gms.entity.*;
import fpt.edu.vn.gms.mapper.AppointmentMapper;
import fpt.edu.vn.gms.repository.*;
import fpt.edu.vn.gms.service.AppointmentService;
import fpt.edu.vn.gms.service.CodeSequenceService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AppointmentServiceImpl implements AppointmentService {

    VehicleRepository vehicleRepo;
    CustomerRepository customerRepo;
    TimeSlotRepository timeSlotRepo;
    AppointmentRepository appointmentRepo;
    ServiceTypeRepository serviceTypeRepo;
    DiscountPolicyRepository discountPolicyRepo;
    CodeSequenceService codeSequenceService;

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

        // Lấy giảm giá mặc định
        DiscountPolicy defaultPolicy = discountPolicyRepo.findByLoyaltyLevel(CustomerLoyaltyLevel.BRONZE)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chính sách giảm giá mặc định!"));

        // Check customer theo số điện thoại
        Customer customer = customerRepo.findByPhone(dto.getPhoneNumber())
                .orElseGet(() -> {
                    // Nếu chưa có thì tạo mới customer
                    Customer newCustomer = Customer.builder()
                            .phone(dto.getPhoneNumber())
                            .discountPolicy(defaultPolicy)
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
                            .customer(customer)
                            .build();
                    return vehicleRepo.save(newVehicle);
                });

        // Kiểm tra hợp lệ timeslotId khi truyền từ frontend về
        List<TimeSlot> allSlots = timeSlotRepo.findAll();
        if (dto.getTimeSlotIndex() < 1 || dto.getTimeSlotIndex() > allSlots.size()) {
            throw new IllegalArgumentException("Khung giờ không tồn tại!!!");
        }
        TimeSlot slot = allSlots.get(dto.getTimeSlotIndex() - 1);

        // Kiểm tra khung giờ
        int booked = appointmentRepo.countByAppointmentDateAndTimeSlot(dto.getAppointmentDate(), slot);
        if (booked >= slot.getMaxCapacity()) {
            throw new IllegalArgumentException("Khung giờ bạn đặt đã đầy!!!");
        }

        // Lấy danh sách ServiceType từ danh sách ID
        List<ServiceType> serviceTypes = dto.getServiceType().stream()
                .map(serviceTypeRepo::getById)
                .toList();

        Appointment appointment = Appointment.builder()
                .appointmentCode(codeSequenceService.generateCode("APT"))
                .customer(customer)
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
    public Page<AppointmentResponseDto> getAppointmentsByStatus(AppointmentStatus status, Pageable pageable) {

        Page<Appointment> appointments = appointmentRepo.getByStatus(status, pageable);
        return appointments.map(AppointmentMapper::toDto);

    }

    @Override
    public List<AppointmentBySlotResponse> getAppointmentsByDate(LocalDate date) {

        List<Appointment> appointments = appointmentRepo.findByAppointmentDate(date);

        // Nhóm theo slot
        Map<TimeSlot, List<Appointment>> grouped = appointments.stream()
                .collect(Collectors.groupingBy(Appointment::getTimeSlot));

        return grouped.entrySet().stream()
                .map(entry -> new AppointmentBySlotResponse(
                        entry.getKey().getLabel(),
                        entry.getKey().getStartTime(),
                        entry.getKey().getEndTime(),
                        entry.getValue().size()
                ))
                .sorted(Comparator.comparing(AppointmentBySlotResponse::getStartTime))
                .collect(Collectors.toList());
    }

    @Override
    public AppointmentResponseDto getAppointmentById(Long id) {
        Appointment appointment = appointmentRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found with id: " + id));


        return AppointmentMapper.toDto(appointment);
    }

    @Override
    public AppointmentResponseDto updateArrivedStatus(Long id) {
        Appointment appointment = appointmentRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Không có phiếu với id = " + id));

        appointment.setStatus(AppointmentStatus.ARRIVED);
        appointmentRepo.save(appointment);

        return AppointmentMapper.toDto(appointment);
    }

    @Override
    public long countAppointmentsByDate(LocalDate date) {
        return appointmentRepo.countByDate(date);
    }
}
