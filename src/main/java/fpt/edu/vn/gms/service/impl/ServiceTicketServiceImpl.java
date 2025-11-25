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

    @Transactional
    @Override
    public ServiceTicketResponseDto createServiceTicket(ServiceTicketRequestDto dto, Employee currEmployee) {

        Customer customer = null;
        Vehicle vehicle = null;

        // ----------------------------
        // 1. XỬ LÝ CUSTOMER
        // ----------------------------
        if (dto.getCustomer().getCustomerId() != null) {

            // Lấy customer theo ID
            customer = customerRepository.findById(dto.getCustomer().getCustomerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khách hàng!"));

            // Có customerId → GIỮ NGUYÊN, không update
        } else {

            // Không có customerId → tạo mới
            DiscountPolicy defaultPolicy = discountPolicyRepo.findByLoyaltyLevel(CustomerLoyaltyLevel.BRONZE)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy chính sách giảm giá mặc định!"));

            customer = Customer.builder()
                    .fullName(dto.getCustomer().getFullName())
                    .phone(dto.getCustomer().getPhone())
                    .address(dto.getCustomer().getAddress())
                    .customerType(dto.getCustomer().getCustomerType())
                    .discountPolicy(defaultPolicy)
                    .isActive(true)
                    .build();

            customer = customerRepository.save(customer);
        }

        // ----------------------------
        // 2. XỬ LÝ VEHICLE
        // ----------------------------
        if (dto.getVehicle().getVehicleId() != null) {

            // Lấy vehicle theo ID
            vehicle = vehicleRepository.findById(dto.getVehicle().getVehicleId())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy xe!"));

            // Có vehicleId và KHÔNG có customerId → update vehicle về customer mới
            if (dto.getCustomer().getCustomerId() == null) {
                vehicle.setCustomer(customer);
                vehicleRepository.save(vehicle);
            }

            // Nếu có cả vehicleId + customerId → giữ nguyên, KHÔNG update
        }
        else {

            // Không có vehicleId → tạo mới
            Brand brand = resolveBrand(dto);
            VehicleModel vehicleModel = resolveVehicleModel(dto, brand);

            vehicle = Vehicle.builder()
                    .licensePlate(dto.getVehicle().getLicensePlate())
                    .vehicleModel(vehicleModel)
                    .year(dto.getVehicle().getYear())
                    .vin(dto.getVehicle().getVin())
                    .customer(customer)
                    .build();

            vehicle = vehicleRepository.save(vehicle);
        }

        // ----------------------------
        // 3. Lấy danh sách kỹ thuật viên
        // ----------------------------
        List<Employee> technicians = List.of();
        if (dto.getAssignedTechnicianIds() != null) {
            technicians = employeeRepository.findAllById(dto.getAssignedTechnicianIds());
        }

        // ----------------------------
        // 4. Lấy loại dịch vụ
        // ----------------------------
        List<ServiceType> serviceTypes = List.of();
        if (dto.getServiceTypeIds() != null) {
            serviceTypes = serviceTypeRepository.findAllById(dto.getServiceTypeIds());
        }

        // ----------------------------
        // 5. Kiểm tra có appointment không
        // ----------------------------
        Appointment appointment = null;
        if (dto.getAppointmentId() != null) {
            appointment = appointmentRepository.findById(dto.getAppointmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy lịch hẹn!"));
        }

        // ----------------------------
        // 6. Tạo service ticket
        // ----------------------------
        ServiceTicket ticket = ServiceTicket.builder()
                .serviceTicketCode(codeSequenceService.generateCode("STK"))
                .serviceTypes(serviceTypes)
                .customer(customer)
                .customerName(customer.getFullName())
                .customerPhone(customer.getPhone())
                .vehicle(vehicle)
                .createdBy(currEmployee)
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

    private Brand resolveBrand(ServiceTicketRequestDto dto) {
        if (dto.getVehicle().getBrandId() != null) {
            return brandRepository.findById(dto.getVehicle().getBrandId())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy hãng xe!"));
        }
        return brandRepository.save(Brand.builder()
                .name(dto.getVehicle().getBrandName())
                .build());
    }

    private VehicleModel resolveVehicleModel(ServiceTicketRequestDto dto, Brand brand) {
        if (dto.getVehicle().getModelId() != null) {
            return vehicleModelRepository.findById(dto.getVehicle().getModelId())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy model!"));
        }
        return vehicleModelRepository.save(VehicleModel.builder()
                .brand(brand)
                .name(dto.getVehicle().getModelName())
                .build());
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