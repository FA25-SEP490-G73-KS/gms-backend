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
     * Tạo mới phiếu dịch vụ cho khách vãng lai
     * @param serviceTicketDtoRequest dữ liệu phiếu dịch vụ cần tạo
     * @param employeeIdOfServiceAvidor employee_id của Service Advisor
     * @return
     */
    @Override
    public ServiceTicketDto createServiceTicket(ServiceTicketDto serviceTicketDtoRequest, Long employeeIdOfServiceAvidor) {
        //Kiểm tra đầu vào bắt buộc: licensePlate (biển số xe) luôn luôn phải có.
        if (serviceTicketDtoRequest.getLicensePlate() == null || serviceTicketDtoRequest.getLicensePlate().isBlank()) {
            throw new IllegalArgumentException("licensePlate là bắt buộc");
        }

        Customer customer;

        //Trường hợp đã có sẵn customerId trong request
        if (serviceTicketDtoRequest.getCustomerId() != null) {
            // -> Tìm Customer theo ID. Nếu không tồn tại thì báo lỗi.
            customer = customerRepository.findById(Math.toIntExact(serviceTicketDtoRequest.getCustomerId()))
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Customer không tồn tại với id: " + serviceTicketDtoRequest.getCustomerId()));

        } else {
            //Trường hợp KHÔNG có customerId, bắt buộc phải nhập đầy đủ họ tên và số điện thoại.
            if (serviceTicketDtoRequest.getFullName() == null || serviceTicketDtoRequest.getFullName().isBlank()) {
                throw new IllegalArgumentException("fullName là bắt buộc");
            }
            if (serviceTicketDtoRequest.getPhone() == null || serviceTicketDtoRequest.getPhone().isBlank()) {
                throw new IllegalArgumentException("phone là bắt buộc");
            }
            String phoneError = PhoneUtils.validatePhoneNumber(serviceTicketDtoRequest.getPhone());
            if (phoneError != null) {
                throw new IllegalArgumentException(phoneError);
            }
            //Chuẩn hóa số điện thoại (loại bỏ khoảng trắng thừa)
            String normalizedPhone = serviceTicketDtoRequest.getPhone().replaceAll("\\s+", "");
            //Tìm khách hàng theo số điện thoại:
            // - Nếu đã có trong DB → lấy thông tin đó.
            // - Nếu chưa có → tạo mới một khách hàng với thông tin trong request.
            customer = customerRepository.findByPhone(normalizedPhone)
                    .orElseGet(() -> {
                        //Tạo mới khách hàng
                        Customer c = Customer.builder()
                                .fullName(serviceTicketDtoRequest.getFullName())
                                .phone(normalizedPhone)
                                .zaloId(serviceTicketDtoRequest.getZaloId())
                                .address(serviceTicketDtoRequest.getAddress())
                                .customerType(serviceTicketDtoRequest.getCustomerType())
                                .loyaltyLevel(serviceTicketDtoRequest.getLoyaltyLevel())
                                .build();
                        return customerRepository.save(c);
                    });
        }
        // Xử lý Vehicle (xe):
        // Một biển số có thể thuộc nhiều khách hàng khác nhau (nếu xe đổi chủ).
        // Vì vậy ta phải tìm theo cặp (licensePlate + customerId).
        // - Nếu tồn tại: dùng lại xe đó.
        // - Nếu chưa có: tạo mới Vehicle gắn với customer hiện tại.
        Vehicle vehicle = vehicleRepository
                .findByLicensePlateAndCustomer_CustomerId(
                        serviceTicketDtoRequest.getLicensePlate(),
                        customer.getCustomerId())
                .orElseGet(() -> {
                    //Tạo mới xe gắn với khách hàng hiện tại
                    Vehicle v = Vehicle.builder()
                            .customer(customer)
                            .licensePlate(serviceTicketDtoRequest.getLicensePlate())
                            .brand(serviceTicketDtoRequest.getBrand())
                            .model(serviceTicketDtoRequest.getModel())
                            .year(serviceTicketDtoRequest.getYear())
                            .vin(serviceTicketDtoRequest.getVin())
                            .build();
                    return vehicleRepository.save(v);
                });

        //Tạo mới phiếu dịch vụ (ServiceTicket):
        // - Không có Appointment (đặt lịch) ban đầu → để null.
        // - Mặc định trạng thái ban đầu là "CHỜ BÁO GIÁ".
        // - Thời gian tạo phiếu là thời điểm hiện tại.
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
    public ServiceTicketDto getServiceTicketByServiceTicketId(Long serviceTicketId) {
        ServiceTicket serviceTicket = serviceTicketRepository.findById(serviceTicketId)
                .orElseThrow(() -> new ResourceNotFoundException("ServiceTicket không tồn tại với id: " + serviceTicketId));
        return ServiceTicketMapper.mapToServiceTicketDto(serviceTicket);
    }

    /**
     * Lấy danh sách phiếu dịch vụ có phân trang.
     */
    @Override
    public Page<ServiceTicketDto> getAllServiceTicket(int page, int size) {
        Pageable pageable = Pageable.ofSize(size).withPage(page);
        return serviceTicketRepository.findAll(pageable).map(ServiceTicketMapper::mapToServiceTicketDto);
    }

    /**
     * Cập nhật phiếu dịch vụ theo ID.
     * Chỉ cập nhật các trường có giá trị (khác null) trong DTO.
     */
    @Override
    public ServiceTicketDto updateServiceTicket(Long id, ServiceTicketDto dto) {
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
        return ServiceTicketMapper.mapToServiceTicketDto(saved);


    }
}