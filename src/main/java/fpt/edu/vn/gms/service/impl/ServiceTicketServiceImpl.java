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
import fpt.edu.vn.gms.utils.PhoneUtils;
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

        if (dto.getCustomer().getCustomerId() != null) {

            // Lấy customer theo ID
            customer = customerRepository.findById(dto.getCustomer().getCustomerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy khách hàng!"));


            customer.setAddress(dto.getCustomer().getAddress());
        } else {

            // Không có customerId → tạo mới
            DiscountPolicy defaultPolicy = discountPolicyRepo.findByLoyaltyLevel(CustomerLoyaltyLevel.BRONZE)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy chính sách giảm giá mặc định!"));

            String customerPhone = PhoneUtils.normalize(dto.getCustomer().getPhone());

            customer = Customer.builder()
                    .fullName(dto.getCustomer().getFullName())
                    .phone(customerPhone)
                    .address(dto.getCustomer().getAddress())
                    .customerType(dto.getCustomer().getCustomerType())
                    .discountPolicy(defaultPolicy)
                    .isActive(true)
                    .build();

            customer = customerRepository.save(customer);
        }

        if (dto.getVehicle().getVehicleId() != null) {

            vehicle = vehicleRepository.findById(dto.getVehicle().getVehicleId())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy xe!"));

            // Cập nhật thông tin xe từ DTO
            Brand brand = resolveBrand(dto);
            VehicleModel vehicleModel = resolveVehicleModel(dto, brand);

            vehicle.setVehicleModel(vehicleModel);
            vehicle.setYear(dto.getVehicle().getYear());
            vehicle.setVin(dto.getVehicle().getVin());
            vehicle.setLicensePlate(dto.getVehicle().getLicensePlate());

            // Có vehicleId và KHÔNG có customerId → update vehicle về customer mới
            if (dto.getCustomer().getCustomerId() == null) {
                vehicle.setCustomer(customer);
                vehicleRepository.save(vehicle);
            } else if (vehicle != null && vehicle.getCustomer() != null && !vehicle.getCustomer().getCustomerId().equals(customer.getCustomerId())) {
                // Trường hợp biển số thuộc khách hàng khác → cập nhật lại chủ xe là customer hiện tại

                if (!dto.getForceAssignVehicle()) {
                    throw new RuntimeException("Biển số thuộc khách hàng khác");
                }

                vehicle.setCustomer(customer);
                vehicleRepository.save(vehicle);
            }


            // Nếu có cả vehicleId + customerId → giữ nguyên, KHÔNG update
        }
        else {

            // Không có vehicleId → tìm theo biển số, nếu chưa tồn tại thì tạo mới
            vehicle = vehicleRepository.findByLicensePlate(dto.getVehicle().getLicensePlate())
                    .orElse(null);

            if (vehicle != null) {
                if (vehicle.getCustomer() == null || !vehicle.getCustomer().getCustomerId().equals(customer.getCustomerId())) {
                    vehicle.setCustomer(customer);
                    vehicleRepository.save(vehicle);
                }
            } else {
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
                .serviceTicketCode(codeSequenceService.generateCode("DV"))
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

        ServiceTicket serviceTicket = serviceTicketRepository.findDetail(serviceTicketId)
                .orElseThrow(() -> new ResourceNotFoundException("ServiceTicket không tồn tại với id: " + serviceTicketId));

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

        existing.setDeliveryAt(dto.getDeliveryAt());

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

    @Transactional
    @Override
    public ServiceTicketResponseDto updateStatus(Long id, ServiceTicketStatus newStatus) {
        ServiceTicket ticket = serviceTicketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phiếu dịch vụ với ID: " + id));

        ServiceTicketStatus current = ticket.getStatus();

        // Cho phép chuyển sang CANCELED từ bất kỳ trạng thái nào
        if (newStatus == ServiceTicketStatus.CANCELED) {
            ticket.setStatus(ServiceTicketStatus.CANCELED);
        } else if (newStatus == ServiceTicketStatus.WAITING_FOR_DELIVERY) {
            // Chỉ từ CHỜ BÁO GIÁ → CHỜ BÀN GIAO XE
            if (current != ServiceTicketStatus.WAITING_FOR_QUOTATION) {
                throw new IllegalStateException("Chỉ phiếu ở trạng thái 'Chờ báo giá' mới được chuyển sang 'Chờ bàn giao xe'");
            }
            ticket.setStatus(ServiceTicketStatus.WAITING_FOR_DELIVERY);
        } else if (newStatus == ServiceTicketStatus.COMPLETED) {
            // Chỉ từ CHỜ BÀN GIAO XE → HOÀN THÀNH
            if (current != ServiceTicketStatus.WAITING_FOR_DELIVERY) {
                throw new IllegalStateException("Chỉ phiếu ở trạng thái 'Chờ bàn giao xe' mới được chuyển sang 'Hoàn thành'");
            }
            ticket.setStatus(ServiceTicketStatus.COMPLETED);
        } else {
            // Không cho phép chuyển trực tiếp về CREATED hoặc trạng thái không được định nghĩa trong rule
            throw new IllegalArgumentException("Không được phép chuyển sang trạng thái: " + newStatus);
        }

        ServiceTicket saved = serviceTicketRepository.save(ticket);
        return serviceTicketMapper.toResponseDto(saved);
    }
}
