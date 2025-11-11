package fpt.edu.vn.gms.service.impl;


import fpt.edu.vn.gms.common.AppointmentStatus;
import fpt.edu.vn.gms.common.CustomerLoyaltyLevel;
import fpt.edu.vn.gms.common.CustomerType;

import fpt.edu.vn.gms.common.PriceQuotationStatus;

import fpt.edu.vn.gms.common.ServiceTicketStatus;
import fpt.edu.vn.gms.dto.request.ServiceTicketRequestDto;
import fpt.edu.vn.gms.dto.request.VehicleRequestDto;
import fpt.edu.vn.gms.dto.response.PriceQuotationResponseDto;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    private final PriceQuotationRepository priceQuotationRepository;
    private final CodeSequenceService codeSequenceService;

    @Override
    public ServiceTicketResponseDto createServiceTicket(ServiceTicketRequestDto dto) {


        //nếu như tồn tại custumer cùng với tất cả những vehicle liên quan tới customer thì ko cần tạo nữa. ngược lại thì vẫn tạo như bình thường
        // Tìm hoặc tạo Customer theo số điện thoại (coi phone là duy nhất)
        Customer customer = customerRepository.findByPhone(normalizedPhone)
                .orElseGet(() -> {
                    Customer c = Customer.builder()
                            .fullName(serviceTicketDtoRequest.getFullName())
                            .phone(normalizedPhone)
                            .zaloId(serviceTicketDtoRequest.getZaloId())
                            .address(serviceTicketDtoRequest.getAddress())
                            .customerType(serviceTicketDtoRequest.getCustomerType())
                            .loyaltyLevel(serviceTicketDtoRequest.getLoyaltyLevel())
                            .customerType(CustomerType.valueOf(String.valueOf(serviceTicketDtoRequest.getCustomerType())))
                            .loyaltyLevel(CustomerLoyaltyLevel.valueOf(String.valueOf(serviceTicketDtoRequest.getLoyaltyLevel())))
                            .build();
                    return customerRepository.save(c);
                });

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
    public ServiceTicketResponseDto sendQuotationToCustomer(Long serviceTicketId) {

        // Tìm phiếu dịch vụ
        ServiceTicket ticket = serviceTicketRepository.findById(serviceTicketId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phiếu dịch vụ ID: " + serviceTicketId));

        // Lấy báo giá kèm theo
        PriceQuotation quotation = ticket.getPriceQuotation();
        if (quotation == null) {
            throw new IllegalStateException("Phiếu dịch vụ chưa có báo giá để gửi cho khách hàng.");
        }

        // Kiểm tra trạng thái hợp lệ
        if (quotation.getStatus() != PriceQuotationStatus.WAREHOUSE_CONFIRMED) {
            throw new IllegalStateException("Báo giá chưa được kho xác nhận, không thể gửi cho khách hàng.");
        }

        // Cập nhật trạng thái báo giá
        quotation.setStatus(PriceQuotationStatus.WAITING_CUSTOMER_CONFIRM);
        quotation.setUpdatedAt(LocalDateTime.now());
        priceQuotationRepository.save(quotation);

        // (Tùy chọn) Gửi thông báo nội bộ hoặc qua Zalo OA
        // notificationService.createNotificationForCustomer(
        //         ticket.getCustomerPhone(),
        //         "Báo giá từ phiếu dịch vụ " + ticket.getServiceTicketCode() + " đã được gửi, vui lòng kiểm tra Zalo."
        // );

        // Trả về response DTO
        return serviceTicketMapper.toResponseDto(ticket);
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

                .orElseThrow(() -> new ResourceNotFoundException("ServiceTicket không tồn tại với id: " + id));
        // Cập nhật các trường nếu có trong dto
        if (dto.getAppointmentId() != null) {
            Appointment appointment = appointmentRepository.findById((long) Math.toIntExact(dto.getAppointmentId()))
                    .orElseThrow(() -> new ResourceNotFoundException("Appointment không tồn tại với id: " + dto.getAppointmentId()));
            existing.setAppointment(appointment);
        }
        if (dto.getCustomerId() != null) {
            Customer customer = customerRepository.findById(Math.toIntExact(dto.getCustomerId()))
                    .orElseThrow(() -> new ResourceNotFoundException("Customer không tồn tại với id: " + dto.getCustomerId()));
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
}