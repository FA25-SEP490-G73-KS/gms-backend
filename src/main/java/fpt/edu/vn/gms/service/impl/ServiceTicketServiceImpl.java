package fpt.edu.vn.gms.service.impl;

import fpt.edu.vn.gms.common.ServiceTicketStatus;
import fpt.edu.vn.gms.dto.request.ServiceTicketRequestDto;
import fpt.edu.vn.gms.dto.response.ServiceTicketResponseDto;
import fpt.edu.vn.gms.entity.*;
import fpt.edu.vn.gms.exception.ResourceNotFoundException;
import fpt.edu.vn.gms.mapper.ServiceTicketMapper;
import fpt.edu.vn.gms.repository.*;
import fpt.edu.vn.gms.service.CodeSequenceService;
import fpt.edu.vn.gms.service.ServiceTicketService;
import lombok.RequiredArgsConstructor;
import org.hibernate.Hibernate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ServiceTicketServiceImpl implements ServiceTicketService {

    private final ServiceTicketRepository serviceTicketRepository;
    private final AppointmentRepository appointmentRepository;
    private final CustomerRepository customerRepository;
    private final VehicleRepository vehicleRepository;
    private final EmployeeRepository employeeRepository;
    private final ServiceTypeRepository serviceTypeRepository;
    private final ServiceTicketMapper serviceTicketMapper;
    private final VehicleModelRepository vehicleModelRepository;
    private final CodeSequenceService codeSequenceService;

    @Override
    public ServiceTicketResponseDto createServiceTicket(ServiceTicketRequestDto dto) {

        Customer customer = null;

        if (dto.getCustomer() != null && dto.getCustomer().getCustomerId() != null) {
            customer = customerRepository.findById(dto.getCustomer().getCustomerId())
                    .orElse(null);
        }

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

        VehicleModel vehicleModel = vehicleModelRepository.findById(dto.getVehicle().getModelId())
                .orElseThrow(() -> new RuntimeException("Vehicle model not found"));

        if (vehicle == null) {
            // Chưa tồn tại → tạo mới
            vehicle = Vehicle.builder()
                    .licensePlate(dto.getVehicle().getLicensePlate())
                    .vehicleModel(vehicleModel)
                    .year(dto.getVehicle().getYear())
                    .vin(dto.getVehicle().getVin())
                    .customer(customer)
                    .build();
        } else {
            // Đã tồn tại → cập nhật thông tin
            vehicle.setVehicleModel(vehicleModel);
            vehicle.setYear(dto.getVehicle().getYear());
            vehicle.setVin(dto.getVehicle().getVin());
            vehicle.setCustomer(customer);
        }
        vehicleRepository.save(vehicle);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String phone = authentication.getName();
        Employee creator = employeeRepository.findByPhone(phone);

        List<Employee> technicians = List.of();
        if (dto.getAssignedTechnicianIds() != null && !dto.getAssignedTechnicianIds().isEmpty()) {
            technicians = employeeRepository.findAllById(dto.getAssignedTechnicianIds());
        }

        // Lấy danh sách ServiceType từ danh sách ID
        List<ServiceType> serviceTypes = new ArrayList<>();
        if (dto.getServiceTypeIds() != null && !dto.getServiceTypeIds().isEmpty()) {
            serviceTypes = serviceTypeRepository.findAllById(dto.getServiceTypeIds());
            if (serviceTypes.isEmpty()) {
                throw new ResourceNotFoundException("Không tìm thấy loại dịch vụ nào với danh sách ID đã cung cấp");
            }
        }

        // Kiểm tra có được tạo từ appointment k
        Appointment appointment = null;
        if (dto.getAppointmentId() != null) {
            appointment = appointmentRepository.findById(dto.getAppointmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy lịch hẹn ID: " + dto.getAppointmentId()));
        }

        // Tạo mới ServiceTicket
        ServiceTicket ticket = ServiceTicket.builder()
                .serviceTicketCode(codeSequenceService.generateCode("STK"))
                .serviceTypes(serviceTypes)
                .customer(customer)
                .customerName(dto.getCustomer().getFullName())
                .customerPhone(dto.getCustomer().getPhone())
                .vehicle(vehicle)
                .createdBy(creator)
                .technicians(technicians)
                .receiveCondition(dto.getReceiveCondition())
                .createdAt(LocalDateTime.now())
                .deliveryAt(dto.getExpectedDeliveryAt())
                .status(ServiceTicketStatus.CREATED)
                .appointment(appointment)
                .build();

        ServiceTicket saved = serviceTicketRepository.save(ticket);

        return serviceTicketMapper.toResponseDto(saved);
    }

    @Override
    public ServiceTicketResponseDto getServiceTicketById(Long serviceTicketId) {

        ServiceTicket serviceTicket = serviceTicketRepository.findById(serviceTicketId)
                .orElseThrow(() -> new ResourceNotFoundException("ServiceTicket không tồn tại với id: " + serviceTicketId));

        // load quan hệ để tránh LazyInitializationException
        Hibernate.initialize(serviceTicket.getPriceQuotation());
        if (serviceTicket.getPriceQuotation() != null) {
            Hibernate.initialize(serviceTicket.getPriceQuotation().getItems());
            System.out.println(">>> PQ id = " + serviceTicket.getPriceQuotation().getPriceQuotationId());
        }

        return serviceTicketMapper.toResponseDto(serviceTicket);
    }

    @Override
    public Page<ServiceTicketResponseDto> getAllServiceTicket(int page, int size) {
        Pageable pageable = Pageable.ofSize(size).withPage(page);
        return serviceTicketRepository.findAll(pageable).map(serviceTicketMapper::toResponseDto);
    }

    @Override
    public ServiceTicketResponseDto updateServiceTicket(Long id, ServiceTicketRequestDto dto) {

        // Tìm ServiceTicket cũ
        ServiceTicket existing = serviceTicketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phiếu dịch vụ với ID: " + id));

        // Cập nhật thông tin khách hàng (nếu có)
        if (dto.getCustomer() != null) {
            Customer customer = existing.getCustomer();
            if (customer == null || (dto.getCustomer().getCustomerId() != null &&
                    !dto.getCustomer().getCustomerId().equals(customer.getCustomerId()))) {

                customer = customerRepository.findById(dto.getCustomer().getCustomerId())
                        .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khách hàng ID: " + dto.getCustomer().getCustomerId()));
            }

            customer.setFullName(dto.getCustomer().getFullName());
            customer.setPhone(dto.getCustomer().getPhone());
            customer.setAddress(dto.getCustomer().getAddress());
            customer.setCustomerType(dto.getCustomer().getCustomerType());
            customer.setLoyaltyLevel(dto.getCustomer().getLoyaltyLevel());
            customerRepository.save(customer);

            existing.setCustomer(customer);
            existing.setCustomerName(customer.getFullName());
            existing.setCustomerPhone(customer.getPhone());
        }

        // Cập nhật xe
        if (dto.getVehicle() != null) {
            Vehicle vehicle = existing.getVehicle();
            if (vehicle == null) {
                // chưa có xe → tạo mới
                VehicleModel model = vehicleModelRepository.findById(dto.getVehicle().getModelId())
                        .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy mẫu xe ID: " + dto.getVehicle().getModelId()));
                vehicle = Vehicle.builder()
                        .licensePlate(dto.getVehicle().getLicensePlate())
                        .vehicleModel(model)
                        .year(dto.getVehicle().getYear())
                        .vin(dto.getVehicle().getVin())
                        .customer(existing.getCustomer())
                        .build();
            } else {
                // có rồi → update tại chỗ (không tạo mới)
                vehicle.setLicensePlate(dto.getVehicle().getLicensePlate());
                vehicle.setYear(dto.getVehicle().getYear());
                vehicle.setVin(dto.getVehicle().getVin());

                VehicleModel model = vehicleModelRepository.findById(dto.getVehicle().getModelId())
                        .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy mẫu xe ID: " + dto.getVehicle().getModelId()));
                vehicle.setVehicleModel(model);
            }
            vehicleRepository.save(vehicle);
            existing.setVehicle(vehicle);
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String phone = authentication.getName();
        Employee advisor = employeeRepository.findByPhone(phone);

        existing.setCreatedBy(advisor);

        // Cập nhật kỹ thuật viên
        if (dto.getAssignedTechnicianIds() != null) {
            List<Employee> technicians = employeeRepository.findAllById(dto.getAssignedTechnicianIds());
            existing.setTechnicians(technicians);
        }

        // Cập nhật loại dịch vụ
        if (dto.getServiceTypeIds() != null) {
            List<ServiceType> serviceTypes = serviceTypeRepository.findAllById(dto.getServiceTypeIds());
            existing.setServiceTypes(serviceTypes);
        }

        // Cập nhật các thông tin khác
        existing.setReceiveCondition(dto.getReceiveCondition());
        existing.setDeliveryAt(dto.getExpectedDeliveryAt());

        // Lưu lại
        ServiceTicket updated = serviceTicketRepository.save(existing);

        // Trả về response DTO
        return serviceTicketMapper.toResponseDto(updated);
    }

    @Transactional
    @Override
    public ServiceTicketResponseDto updateDeliveryAt(Long serviceTicketId, LocalDate deliveryAt) {

        ServiceTicket serviceTicket = serviceTicketRepository.findById(serviceTicketId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phiếu dịch vụ với id = " + serviceTicketId));

        serviceTicket.setDeliveryAt(deliveryAt);
        return serviceTicketMapper.toResponseDto(serviceTicketRepository.save(serviceTicket));
    }

    @Override
    public Page<ServiceTicketResponseDto> getServiceTicketsByStatus(ServiceTicketStatus status, int page, int size) {

        Pageable pageable = Pageable.ofSize(size).withPage(page);

        // Tìm theo trạng thái + phân trang
        Page<ServiceTicket> ticketPage = serviceTicketRepository.findByStatus(status, pageable);

        // Load dữ liệu tránh LazyInitializationException
        ticketPage.getContent().forEach(ticket -> {
            Hibernate.initialize(ticket.getPriceQuotation());
            if (ticket.getPriceQuotation() != null) {
                Hibernate.initialize(ticket.getPriceQuotation().getItems());
            }
            Hibernate.initialize(ticket.getServiceTypes());
            Hibernate.initialize(ticket.getTechnicians());
        });

        // Trả về Page DTO
        return ticketPage.map(serviceTicketMapper::toResponseDto);
    }

    @Override
    public Page<ServiceTicketResponseDto> getServiceTicketsByCreatedAt(LocalDateTime createdAt, Pageable pageable) {

        Page<ServiceTicket> serviceTicketPage = serviceTicketRepository.findByCreatedAt(createdAt, pageable);

        return serviceTicketPage.map(serviceTicketMapper::toResponseDto);
    }

}