package fpt.edu.vn.gms.service.impl;

import fpt.edu.vn.gms.common.enums.CustomerLoyaltyLevel;
import fpt.edu.vn.gms.common.enums.ServiceTicketStatus;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private final DiscountPolicyRepository discountPolicyRepo;
    private final CodeSequenceService codeSequenceService;
    private final BrandRepository brandRepository;

    @Override
    public ServiceTicketResponseDto createServiceTicket(ServiceTicketRequestDto dto, Employee currEmployee) {

        Customer customer = null;

        if (dto.getCustomer() != null && dto.getCustomer().getCustomerId() != null) {
            customer = customerRepository.findById(dto.getCustomer().getCustomerId())
                    .orElse(null);
        }

        // Lấy giảm giá mặc định
        DiscountPolicy defaultPolicy = discountPolicyRepo.findByLoyaltyLevel(CustomerLoyaltyLevel.BRONZE)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy chính sách giảm giá mặc định!"));

        if (customer == null) {
            // Chưa tồn tại → tạo mới
            customer = Customer.builder()
                    .fullName(dto.getCustomer().getFullName())
                    .phone(dto.getCustomer().getPhone())
                    .address(dto.getCustomer().getAddress())
                    .customerType(dto.getCustomer().getCustomerType())
                    .discountPolicy(defaultPolicy)
                    .build();
        } else {
            // Đã tồn tại → cập nhật thông tin (nếu có thay đổi)
            customer.setFullName(dto.getCustomer().getFullName());
            customer.setAddress(dto.getCustomer().getAddress());
            customer.setCustomerType(dto.getCustomer().getCustomerType());
        }

        customer = customerRepository.save(customer);

        Vehicle vehicle = vehicleRepository.findByLicensePlate(dto.getVehicle().getLicensePlate()).orElse(null);

        VehicleModel vehicleModel;
        Brand brand;

        // Xử lý brand
        if (dto.getVehicle().getBrandId() != null) {
            // Brand đã tồn tại → lấy từ DB
            brand = brandRepository.findById(dto.getVehicle().getBrandId())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy hãng xe!"));
        } else {
            // Brand chưa có → tạo mới
            brand = Brand.builder()
                    .name(dto.getVehicle().getBrandName())
                    .build();
            brand = brandRepository.save(brand);
        }

        // Xử lý vehicle model
        if (dto.getVehicle().getModelId() != null) {
            // Client gửi modelId → lấy model có sẵn
            vehicleModel = vehicleModelRepository.findById(dto.getVehicle().getModelId())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy loại xe!"));
        } else {
            // Chưa có modelId → tạo mới
            VehicleModel newModel = VehicleModel.builder()
                    .brand(brand)
                    .name(dto.getVehicle().getModelName())
                    .build();
            vehicleModel = vehicleModelRepository.save(newModel);
        }

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
                .createdBy(currEmployee)
                .technicians(technicians)
                .receiveCondition(dto.getReceiveCondition())
                .createdAt(LocalDateTime.now())
                .deliveryAt(dto.getExpectedDeliveryAt())
                .status(ServiceTicketStatus.CREATED)
                .appointment(appointment)
                .build();

        System.out.println(currEmployee);

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

    @Transactional
    @Override
    public ServiceTicketResponseDto updateServiceTicket(Long id, ServiceTicketRequestDto dto) {

        ServiceTicket existing = serviceTicketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phiếu dịch vụ với ID: " + id));

        // --- CUSTOMER ---
        Customer customer = existing.getCustomer();
        if (dto.getCustomer() != null) {
            if (customer == null) {
                // Chưa có customer → tạo mới
                DiscountPolicy defaultPolicy = discountPolicyRepo.findByLoyaltyLevel(CustomerLoyaltyLevel.BRONZE)
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy chính sách giảm giá mặc định!"));

                customer = Customer.builder()
                        .fullName(dto.getCustomer().getFullName())
                        .phone(dto.getCustomer().getPhone())
                        .address(dto.getCustomer().getAddress())
                        .customerType(dto.getCustomer().getCustomerType())
                        .discountPolicy(defaultPolicy)
                        .build();
            } else {
                // Cập nhật thông tin khách hàng
                customer.setFullName(dto.getCustomer().getFullName());
                customer.setPhone(dto.getCustomer().getPhone());
                customer.setAddress(dto.getCustomer().getAddress());
                customer.setCustomerType(dto.getCustomer().getCustomerType());
            }
            customer = customerRepository.save(customer);

            existing.setCustomer(customer);
            existing.setCustomerName(customer.getFullName());
            existing.setCustomerPhone(customer.getPhone());
        }

        // --- VEHICLE, BRAND, VEHICLE MODEL ---
        if (dto.getVehicle() != null) {
            Vehicle vehicle = existing.getVehicle();
            VehicleModel vehicleModel;
            Brand brand;

            // Xử lý brand
            if (dto.getVehicle().getBrandId() != null) {
                brand = brandRepository.findById(dto.getVehicle().getBrandId())
                        .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy hãng xe!"));
            } else {
                brand = Brand.builder().name(dto.getVehicle().getBrandName()).build();
                brand = brandRepository.save(brand);
            }

            // Xử lý vehicle model
            if (dto.getVehicle().getModelId() != null) {
                vehicleModel = vehicleModelRepository.findById(dto.getVehicle().getModelId())
                        .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy loại xe!"));
            } else {
                VehicleModel newModel = VehicleModel.builder()
                        .brand(brand)
                        .name(dto.getVehicle().getModelName())
                        .build();
                vehicleModel = vehicleModelRepository.save(newModel);
            }

            // Tạo hoặc cập nhật vehicle
            if (vehicle == null) {
                vehicle = Vehicle.builder()
                        .licensePlate(dto.getVehicle().getLicensePlate())
                        .vehicleModel(vehicleModel)
                        .year(dto.getVehicle().getYear())
                        .vin(dto.getVehicle().getVin())
                        .customer(customer)
                        .build();
            } else {
                vehicle.setVehicleModel(vehicleModel);
                vehicle.setLicensePlate(dto.getVehicle().getLicensePlate());
                vehicle.setYear(dto.getVehicle().getYear());
                vehicle.setVin(dto.getVehicle().getVin());
                vehicle.setCustomer(customer);
            }
            vehicleRepository.save(vehicle);
            existing.setVehicle(vehicle);
        }

        // --- TECHNICIANS ---
        if (dto.getAssignedTechnicianIds() != null) {
            List<Employee> technicians = employeeRepository.findAllById(dto.getAssignedTechnicianIds());
            existing.setTechnicians(technicians);
        }

        // --- SERVICE TYPES ---
        if (dto.getServiceTypeIds() != null) {
            List<ServiceType> serviceTypes = serviceTypeRepository.findAllById(dto.getServiceTypeIds());
            existing.setServiceTypes(serviceTypes);
        }

        // Lưu lại phiếu dịch vụ
        serviceTicketRepository.save(existing);

        return serviceTicketMapper.toResponseDto(existing);
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

    @Override
    public long countServiceTicketByDate(LocalDate date) {
        return serviceTicketRepository.countByDate(date);
    }

    @Override
    public List<Map<String, Object>> getCompletedTicketsByMonth() {

        List<Object[]> results = serviceTicketRepository.countCompletedTicketsGroupedByMonth("COMPLETED");

        // Convert Object[] -> Map để dễ trả về JSON
        List<Map<String, Object>> response = new ArrayList<>();
        for (Object[] row : results) {
            Map<String, Object> map = new HashMap<>();
            map.put("year", row[0]);
            map.put("month", row[1]);
            map.put("count", row[2]);
            response.add(map);
        }
        return response;
    }

    @Override
    public List<Map<String, Object>> getTicketCountsByType(int year, int month) {

        List<Object[]> results = serviceTicketRepository.countTicketsByTypeForMonth(year, month);

        return results.stream().map(obj -> {
            Map<String, Object> map = new HashMap<>();
            map.put("type", obj[0]);   // String (t.name)
            map.put("count", obj[1]);  // Long
            return map;
        }).toList();
    }


}