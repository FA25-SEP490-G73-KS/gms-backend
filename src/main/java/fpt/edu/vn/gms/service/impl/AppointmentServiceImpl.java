package fpt.edu.vn.gms.service.impl;

import fpt.edu.vn.gms.common.AppointmentStatus;
import fpt.edu.vn.gms.dto.AppointmentRequestDto;
import fpt.edu.vn.gms.dto.AppointmentResponseDto;
import fpt.edu.vn.gms.dto.TimeSlotDto;
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

        // Check vehicle
        Vehicle vehicle = vehicleRepo.findByLicensePlate(dto.getLicensePlate())
                .orElseGet(() -> {
                    Customer customer = Customer.builder()
                            .fullName(dto.getCustomerName())
                            .phone(dto.getPhoneNumber())
                            .build();
                    customer = customerRepo.save(customer);

                    Vehicle v = Vehicle.builder()
                            .licensePlate(dto.getLicensePlate())
                            .customer(customer)
                            .build();
                    return vehicleRepo.save(v);
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

        return AppointmentResponseDto.builder()
                .appointmentId(saved.getAppointmentId())
                .customerName(vehicle.getCustomer().getFullName())
                .licensePlate(vehicle.getLicensePlate())
                .appointmentDate(saved.getAppointmentDate())
                .timeSlotLabel(slot.getLabel())
                .serviceType(saved.getServiceType())
                .status(saved.getStatus())
                .note(saved.getDescription())
                .build();
    }

    @Override
    public List<AppointmentResponseDto> getAllAppointments() {
        return appointmentRepo.findAll().stream()
                .map(AppointmentMapper::toDto)
                .collect(Collectors.toList());
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
