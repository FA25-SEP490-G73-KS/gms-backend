package fpt.edu.vn.gms.service.impl;

import fpt.edu.vn.gms.common.enums.CustomerLoyaltyLevel;
import fpt.edu.vn.gms.common.enums.ServiceTicketStatus;
import fpt.edu.vn.gms.dto.request.ServiceTicketRequestDto;
import fpt.edu.vn.gms.dto.request.TicketUpdateReqDto;
import fpt.edu.vn.gms.dto.response.ServiceTicketResponseDto;
import fpt.edu.vn.gms.entity.*;
import fpt.edu.vn.gms.exception.ResourceNotFoundException;
import fpt.edu.vn.gms.mapper.ServiceTicketMapper;
import fpt.edu.vn.gms.repository.*;
import fpt.edu.vn.gms.service.CodeSequenceService;
import fpt.edu.vn.gms.service.ServiceTicketService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ServiceTicketServiceImpl implements ServiceTicketService {

    ServiceTicketRepository serviceTicketRepository;
    AppointmentRepository appointmentRepository;
    CustomerRepository customerRepository;
    VehicleRepository vehicleRepository;
    EmployeeRepository employeeRepository;
    ServiceTypeRepository serviceTypeRepository;
    ServiceTicketMapper serviceTicketMapper;
    VehicleModelRepository vehicleModelRepository;
    DiscountPolicyRepository discountPolicyRepo;
    CodeSequenceService codeSequenceService;
    BrandRepository brandRepository;

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
    public ServiceTicketResponseDto updateServiceTicket(Long id, TicketUpdateReqDto dto) {

        log.info("Update service ticket start, ticketId={}", id);
        log.debug("Incoming request DTO: {}", dto);

        ServiceTicket existing = serviceTicketRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Service ticket not found, id={}", id);
                    return new ResourceNotFoundException("Không tìm thấy phiếu dịch vụ với ID: " + id);
                });

        // ==========================
        // 1. UPDATE CUSTOMER
        // ==========================
        log.info("Updating customer info for ticketId={}", id);

        Customer customer = existing.getCustomer();
        log.debug("Customer before update: {}", customer);

        customer.setFullName(dto.getCustomerName());
        customer.setPhone(dto.getCustomerPhone());
        customerRepository.save(customer);

        existing.setCustomerName(customer.getFullName());
        existing.setCustomerPhone(customer.getPhone());

        log.debug("Customer after update: {}", customer);

        // ==========================
        // 2. UPDATE VEHICLE
        // ==========================
        Vehicle currentVehicle = existing.getVehicle();
        log.debug("Current vehicle: {}", currentVehicle);

        // CASE 1: FE gửi vehicleId → đổi xe khác
        if (dto.getVehicleId() != null) {

            log.info("Switching vehicle, new vehicleId={}", dto.getVehicleId());

            Vehicle newVehicle = vehicleRepository.findById(dto.getVehicleId())
                    .orElseThrow(() -> {
                        log.error("Vehicle not found, id={}", dto.getVehicleId());
                        return new ResourceNotFoundException("Không tìm thấy xe với ID: " + dto.getVehicleId());
                    });

            existing.setVehicle(newVehicle);
            existing.setVehicleLicensePlate(newVehicle.getLicensePlate());

            log.info("Switched to vehicleId={} with plate={}",
                    newVehicle.getVehicleId(), newVehicle.getLicensePlate());
        }

        // CASE 2: Không có vehicleId nhưng có biển số → update vào vehicle hiện tại
        else if (dto.getLicensePlate() != null) {

            log.info("Updating license plate on current vehicle, new plate={}", dto.getLicensePlate());

            if (currentVehicle == null) {
                log.error("Ticket has no vehicle assigned, cannot update license plate");
                throw new IllegalArgumentException("Không thể cập nhật biển số vì ticket chưa gắn xe!");
            }

            log.debug("Vehicle before update: {}", currentVehicle);

            currentVehicle.setLicensePlate(dto.getLicensePlate());

            if (dto.getVin() != null)
                currentVehicle.setVin(dto.getVin());

            vehicleRepository.save(currentVehicle);

            existing.setVehicle(currentVehicle);
            existing.setVehicleLicensePlate(currentVehicle.getLicensePlate());

            log.info("Updated current vehicle license plate successfully, vehicleId={}, plate={}",
                    currentVehicle.getVehicleId(), currentVehicle.getLicensePlate());
        }

        // ==========================
        // 3. UPDATE TECHNICIAN
        // ==========================
        if (dto.getAssignedTechnicianId() != null && !dto.getAssignedTechnicianId().isEmpty()) {

            log.info("Updating technicians, technicianIds={}", dto.getAssignedTechnicianId());

            List<Employee> technicians = employeeRepository.findAllById(dto.getAssignedTechnicianId());

            if (technicians.size() != dto.getAssignedTechnicianId().size()) {
                log.warn("Some technician IDs do not exist. Requested={}, Found={}",
                        dto.getAssignedTechnicianId().size(), technicians.size());
            }

            existing.setTechnicians(technicians);

            log.info("Updated {} technicians for ticketId={}", technicians.size(), id);
        }

        // ==========================
        // 4. UPDATE SERVICE TYPES
        // ==========================
        if (dto.getServiceTypeIds() != null) {

            log.info("Updating service types: {}", dto.getServiceTypeIds());

            List<ServiceType> serviceTypes = serviceTypeRepository.findAllById(dto.getServiceTypeIds());
            existing.setServiceTypes(serviceTypes);

            log.info("Updated {} service types for ticketId={}", serviceTypes.size(), id);
        }

        // SAVE
        serviceTicketRepository.save(existing);

        log.info("Update service ticket completed successfully, ticketId={}", id);

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