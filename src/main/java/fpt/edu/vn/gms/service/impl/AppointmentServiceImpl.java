package fpt.edu.vn.gms.service.impl;

import fpt.edu.vn.gms.common.AppointmentStatus;
import fpt.edu.vn.gms.common.CustomerLoyaltyLevel;
import fpt.edu.vn.gms.dto.request.AppointmentRequestDto;
import fpt.edu.vn.gms.dto.response.AppointmentResponseDto;
import fpt.edu.vn.gms.dto.response.TimeSlotDto;
import fpt.edu.vn.gms.entity.Appointment;
import fpt.edu.vn.gms.entity.Customer;
import fpt.edu.vn.gms.entity.TimeSlot;
import fpt.edu.vn.gms.entity.Vehicle;
import fpt.edu.vn.gms.mapper.AppointmentMapper;
import fpt.edu.vn.gms.repository.AppointmentRepository;
import fpt.edu.vn.gms.repository.CustomerRepository;
import fpt.edu.vn.gms.repository.TimeSlotRepository;
import fpt.edu.vn.gms.repository.VehicleRepository;
import fpt.edu.vn.gms.service.AppointmentService;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {

    private final VehicleRepository vehicleRepo;
    private final CustomerRepository customerRepo;
    private final TimeSlotRepository timeSlotRepo;
    private final AppointmentRepository appointmentRepo;

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
                            .fullName(dto.getCustomerName())
                            .phone(dto.getPhoneNumber())
                            .loyaltyLevel(CustomerLoyaltyLevel.NORMAL)
                            .build();
                    return customerRepo.save(newCustomer);
                });

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

        Appointment appointment = Appointment.builder()
                .customer(vehicle.getCustomer())
                .vehicle(vehicle)
                .timeSlot(slot)
                .appointmentDate(dto.getAppointmentDate())
                .serviceType(dto.getServiceType())
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
    public AppointmentResponseDto getAppointmentById(Long id) {
        Appointment appointment = appointmentRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found with id: " + id));

        return AppointmentMapper.toDto(appointment);
    }

    @Override
    public AppointmentResponseDto updateStatus(Long id, AppointmentStatus status) {
        Appointment appointment = appointmentRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Appointment not found with id: " + id));

        if (status == AppointmentStatus.OVERDUE) {
            throw new IllegalArgumentException("OVERDUE is handled automatically by system");
        }

        // Validate allowed transitions (simple example)
        if (appointment.getStatus() == AppointmentStatus.CANCELLED) {
            throw new IllegalStateException("Cannot change status of a cancelled appointment");
        }

        appointment.setStatus(status);
        appointmentRepo.save(appointment);
        return AppointmentMapper.toDto(appointment);
    }
}
