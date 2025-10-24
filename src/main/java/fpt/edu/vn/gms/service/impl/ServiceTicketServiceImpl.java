package fpt.edu.vn.gms.service.impl;

import fpt.edu.vn.gms.common.ServiceTicketStatus;
import fpt.edu.vn.gms.dto.ServiceTicketDto;
import fpt.edu.vn.gms.dto.request.ServiceTicketRequestDto;
import fpt.edu.vn.gms.dto.response.ServiceTicketResponseDto;
import fpt.edu.vn.gms.entity.*;
import fpt.edu.vn.gms.exception.ResourceNotFoundException;
import fpt.edu.vn.gms.mapper.ServiceTicketMapper;
import fpt.edu.vn.gms.repository.*;
import fpt.edu.vn.gms.service.ServiceTicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ServiceTicketServiceImpl implements ServiceTicketService {

    private final ServiceTicketRepository serviceTicketRepository;
    private final AppointmentRepository appointmentRepository;
    private final CustomerRepository customerRepository;
    private final VehicleRepository vehicleRepository;
    private final EmployeeRepository employeeRepository;
    private final ServiceTicketMapper serviceTicketMapper;

    @Override
    public ServiceTicketResponseDto createServiceTicket(ServiceTicketRequestDto dto) {

        Customer customer = customerRepository.findByPhone(dto.getCustomer().getPhone())
                .orElse(null);

        if (customer == null) {
            // Chưa tồn tại → tạo mới
            customer = Customer.builder()
                    .fullName(dto.getCustomer().getFullName())
                    .phone(dto.getCustomer().getPhone())
                    .address(dto.getCustomer().getAddress())
                    .customerType(dto.getCustomer().getCustomerType())
                    .loyaltyLevel(dto.getCustomer().getLoyaltyLevel())
                    .build();
        } else {
            // Đã tồn tại → cập nhật thông tin (nếu có thay đổi)
            customer.setFullName(dto.getCustomer().getFullName());
            customer.setAddress(dto.getCustomer().getAddress());
            customer.setCustomerType(dto.getCustomer().getCustomerType());
            customer.setLoyaltyLevel(dto.getCustomer().getLoyaltyLevel());
        }

        Vehicle vehicle = vehicleRepository.findByLicensePlate(dto.getVehicle().getLicensePlate()).orElse(null);
        if (vehicle == null) {
            // Chưa tồn tại → tạo mới
            vehicle = Vehicle.builder()
                    .licensePlate(dto.getVehicle().getLicensePlate())
                    .brand(dto.getVehicle().getBrand())
                    .model(dto.getVehicle().getModel())
                    .year(dto.getVehicle().getYear())
                    .vin(dto.getVehicle().getVin())
                    .customer(customer) // gán luôn customer mới
                    .build();
        } else {
            // Đã tồn tại → cập nhật thông tin
            vehicle.setBrand(dto.getVehicle().getBrand());
            vehicle.setModel(dto.getVehicle().getModel());
            vehicle.setYear(dto.getVehicle().getYear());
            vehicle.setVin(dto.getVehicle().getVin());
            vehicle.setCustomer(customer);
        }
        vehicleRepository.save(vehicle);

        Employee advisor = null;
        if (dto.getAdvisorId() != null) {
            advisor = employeeRepository.findById(dto.getAdvisorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nhân viên tư vấn ID: " + dto.getAdvisorId()));
        }

        List<Employee> technicians = List.of();
        if (dto.getAssignedTechnicianIds() != null && !dto.getAssignedTechnicianIds().isEmpty()) {
            technicians = employeeRepository.findAllById(dto.getAssignedTechnicianIds());
        }

        ServiceTicket ticket = ServiceTicket.builder()
                .customer(customer)
                .vehicle(vehicle)
                .serviceAdvisor(advisor)
                .technicians(technicians)
                .receiveCondition(dto.getReceiveCondition())
                .notes(dto.getNote())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .deliveryAt(dto.getExpectedDeliveryAt())
                .status(ServiceTicketStatus.CHO_BAO_GIA)
                .build();

        ServiceTicket saved = serviceTicketRepository.save(ticket);

        return serviceTicketMapper.toResponseDto(saved);
    }

    @Override
    public ServiceTicketResponseDto getServiceTicketById(Long serviceTicketId) {
        ServiceTicket serviceTicket = serviceTicketRepository.findById(serviceTicketId)
                .orElseThrow(() -> new ResourceNotFoundException("ServiceTicket không tồn tại với id: " + serviceTicketId));
        return serviceTicketMapper.toResponseDto(serviceTicket);
    }

    @Override
    public Page<ServiceTicketResponseDto> getAllServiceTicket(int page, int size) {
        Pageable pageable = Pageable.ofSize(size).withPage(page);
        return serviceTicketRepository.findAll(pageable).map(serviceTicketMapper::toResponseDto);
    }

    @Override
    public ServiceTicketResponseDto updateServiceTicket(Long id, ServiceTicketDto dto) {
        ServiceTicket existing = serviceTicketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ServiceTicket không tồn tại với id: " + id));
        // Cập nhật các trường nếu có trong dto
        if (dto.getAppointmentId() != null) {
            Appointment appointment = appointmentRepository.findById(dto.getAppointmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Appointment không tồn tại với id: " + dto.getAppointmentId()));
            existing.setAppointment(appointment);
        }
        if (dto.getCustomerId() != null) {
            Customer customer = customerRepository.findById(Math.toIntExact(dto.getCustomerId()))
                    .orElseThrow(() -> new ResourceNotFoundException("Customer không tồn tại với id: " + dto.getCustomerId()));
            existing.setCustomer(customer);
        }
        if (dto.getVehicleId() != null) {
            Vehicle vehicle = vehicleRepository.findById(Math.toIntExact(dto.getVehicleId()))
                    .orElseThrow(() -> new ResourceNotFoundException("Vehicle không tồn tại với id: " + dto.getVehicleId()));
            existing.setVehicle(vehicle);
        }
        if (dto.getNotes() != null) {
            existing.setNotes(dto.getNotes());
        }
        if (dto.getStatus() != null) {
            existing.setStatus(dto.getStatus());
        }
        if (dto.getDeliveryAt() != null) {
            existing.setDeliveryAt(dto.getDeliveryAt());
        }
        ServiceTicket saved = serviceTicketRepository.save(existing);
        return serviceTicketMapper.toResponseDto(saved);
    }

    @Override
    public ServiceTicketResponseDto createServiceTicketFromAppointment(Long appointmentId, ServiceTicketRequestDto dto) {

        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Appointment ID: " + appointmentId));

        // Lấy thông tin customer và vehicle từ appointment
        Customer customer = appointment.getCustomer();
        Vehicle vehicle = appointment.getVehicle();

        // Cập nhật thông tin của người dùng nếu nhập bổ sung
        if (dto.getCustomer() != null) {
            customer.setFullName(dto.getCustomer().getFullName());
            customer.setPhone(dto.getCustomer().getPhone());
            customer.setAddress(dto.getCustomer().getAddress());
            customer.setCustomerType(dto.getCustomer().getCustomerType());
            customer.setLoyaltyLevel(dto.getCustomer().getLoyaltyLevel());
            customerRepository.save(customer);
        }

        if (dto.getVehicle() != null) {
            vehicle.setLicensePlate(dto.getVehicle().getLicensePlate());
            vehicle.setBrand(dto.getVehicle().getBrand());
            vehicle.setModel(dto.getVehicle().getModel());
            vehicle.setYear(dto.getVehicle().getYear());
            vehicle.setVin(dto.getVehicle().getVin());
            vehicleRepository.save(vehicle);
        }

        // Gán cố vấn dịch vụ (advisor)
        Employee advisor = null;
        if (dto.getAdvisorId() != null) {
            advisor = employeeRepository.findById(dto.getAdvisorId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy nhân viên tư vấn ID: " + dto.getAdvisorId()));
        }

        // Gán danh sách kỹ thuật viên
        List<Employee> technicians = List.of();
        if (dto.getAssignedTechnicianIds() != null && !dto.getAssignedTechnicianIds().isEmpty()) {
            technicians = employeeRepository.findAllById(dto.getAssignedTechnicianIds());
        }

        // Tạo service ticket
        ServiceTicket ticket = ServiceTicket.builder()
                .appointment(appointment)
                .customer(customer)
                .vehicle(vehicle)
                .serviceAdvisor(advisor)
                .technicians(technicians)
                .receiveCondition(dto.getReceiveCondition())
                .notes(dto.getNote())
                .deliveryAt(dto.getExpectedDeliveryAt())
                .status(ServiceTicketStatus.CHO_BAO_GIA)
                .build();

        ServiceTicket saved = serviceTicketRepository.save(ticket);

        // Gán service ticket vào appointment
        appointment.setServiceTicket(saved);
        appointmentRepository.save(appointment);

        // Trả về Dto
        return serviceTicketMapper.toResponseDto(saved);
    }

}