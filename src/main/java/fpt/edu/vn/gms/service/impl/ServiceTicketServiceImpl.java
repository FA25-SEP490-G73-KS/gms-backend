package fpt.edu.vn.gms.service.impl;

import fpt.edu.vn.gms.common.AppointmentStatus;
import fpt.edu.vn.gms.common.ServiceTicketStatus;
import fpt.edu.vn.gms.dto.ServiceTicketDto;
import fpt.edu.vn.gms.entity.*;
import fpt.edu.vn.gms.exception.ResourceNotFoundException;
import fpt.edu.vn.gms.mapper.ServiceTicketMapper;
import fpt.edu.vn.gms.repository.*;
import fpt.edu.vn.gms.service.ServiceTicketService;
import fpt.edu.vn.gms.utils.PhoneUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Triển khai ServiceTicketService.
 * Chứa logic nghiệp vụ cho CRUD và tìm kiếm Phiếu Dịch Vụ, làm việc với Repository và Mapper.
 */
@Service
@RequiredArgsConstructor
public class ServiceTicketServiceImpl implements ServiceTicketService {
    private final ServiceTicketRepository serviceTicketRepository;
    private final AppointmentRepository appointmentRepository;
    private final CustomerRepository customerRepository;
    private final VehicleRepository vehicleRepository;
    private final AssignmentRepository AssignmentRepository;

    /**
     * Tạo mới phiếu dịch vụ cùng lúc với tạo Customer và Vehicle
     *
     * @param serviceTicketDtoRequest
     * @return
     */
    @Override
    public ServiceTicketDto createNewServiceTicket(ServiceTicketDto serviceTicketDtoRequest) {
        // Validate minimal required fields
        if (serviceTicketDtoRequest == null) {
            throw new IllegalArgumentException("Thiếu dữ liệu yêu cầu");
        }
        if (serviceTicketDtoRequest.getFullName() == null || serviceTicketDtoRequest.getFullName().isBlank()) {
            throw new IllegalArgumentException("fullName là bắt buộc");
        }
        if (serviceTicketDtoRequest.getPhone() == null || serviceTicketDtoRequest.getPhone().isBlank()) {
            throw new IllegalArgumentException("phone là bắt buộc");
        }
        if (serviceTicketDtoRequest.getLicensePlate() == null || serviceTicketDtoRequest.getLicensePlate().isBlank()) {
            throw new IllegalArgumentException("licensePlate là bắt buộc");
        }
        // Validate số điện thoại theo quy tắc hệ thống
        String phoneError = PhoneUtils.validatePhoneNumber(serviceTicketDtoRequest.getPhone());
        if (phoneError != null) {
            throw new IllegalArgumentException(phoneError);
        }
        String normalizedPhone = serviceTicketDtoRequest.getPhone().replaceAll("\\s+", "");

        // Tạo Customer
        Customer customer = Customer.builder()
                .fullName(serviceTicketDtoRequest.getFullName())
                .phone(normalizedPhone)
                .zaloId(serviceTicketDtoRequest.getZaloId()) // để null nếu không có
                .address(serviceTicketDtoRequest.getAddress()) // để null nếu không có
                .customerType(String.valueOf(serviceTicketDtoRequest.getCustomerType()))
                .loyaltyLevel(String.valueOf(serviceTicketDtoRequest.getLoyaltyLevel()))
                .build();
        customer = customerRepository.save(customer);

        // Tạo Vehicle
        Vehicle vehicle = Vehicle.builder()
                .customer(customer)
                .licensePlate(serviceTicketDtoRequest.getLicensePlate())
                .brand(serviceTicketDtoRequest.getBrand())
                .model(serviceTicketDtoRequest.getModel())
                .year(serviceTicketDtoRequest.getYear())
                .vin(serviceTicketDtoRequest.getVin())
                .build();
        vehicle = vehicleRepository.save(vehicle);


        // Tạo ServiceTicket
        LocalDateTime now = LocalDateTime.now();
        ServiceTicket st = new ServiceTicket();
        st.setAppointment(null);
        st.setCustomer(customer);
        st.setVehicle(vehicle);
        st.setCreatedAt(now);
        st.setDeliveryAt(null);
        st.setNotes(null);
        st.setStatus(ServiceTicketStatus.CHO_BAO_GIA);
        ServiceTicket saved = serviceTicketRepository.save(st);
        return ServiceTicketMapper.mapToServiceTicketDto(saved);


    }

    /**
     * Lấy phiếu dịch vụ theo ID, ném lỗi 404 nếu không tồn tại.
     */
    @Override
    public ServiceTicketDto getById(Long id) {
        ServiceTicket st = serviceTicketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ServiceTicket không tồn tại với id: " + id));
        return ServiceTicketMapper.mapToServiceTicketDto(st);
    }

    /**
     * Lấy danh sách phiếu dịch vụ có phân trang.
     */
    @Override
    public Page<ServiceTicketDto> getAll(Pageable pageable) {
        return serviceTicketRepository.findAll(pageable).map(ServiceTicketMapper::mapToServiceTicketDto);
    }

    /**
     * Cập nhật phiếu dịch vụ theo ID.
     * Chỉ cập nhật các trường có giá trị (khác null) trong DTO.
     */
    @Override
    public ServiceTicketDto update(Long id, ServiceTicketDto dto) {
        ServiceTicket existing = serviceTicketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ServiceTicket không tồn tại với id: " + id));
        // Cập nhật các trường nếu có trong dto
        if (dto.getAppointmentId() != null) {
            Appointment appointment = appointmentRepository.findById(Math.toIntExact(dto.getAppointmentId()))
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
        return ServiceTicketMapper.mapToServiceTicketDto(saved);


    }
}