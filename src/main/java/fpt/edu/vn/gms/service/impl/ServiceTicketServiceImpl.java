package fpt.edu.vn.gms.service.impl;

import fpt.edu.vn.gms.common.AppointmentStatus;
import fpt.edu.vn.gms.common.ServiceTicketStatus;
import fpt.edu.vn.gms.dto.ServiceTicketDto;
import fpt.edu.vn.gms.entity.Appointment;
import fpt.edu.vn.gms.entity.Customer;
import fpt.edu.vn.gms.entity.ServiceTicket;
import fpt.edu.vn.gms.entity.Vehicle;
import fpt.edu.vn.gms.exception.ResourceNotFoundException;
import fpt.edu.vn.gms.mapper.ServiceTicketMapper;
import fpt.edu.vn.gms.repository.AppointmentRepository;
import fpt.edu.vn.gms.repository.CustomerRepository;
import fpt.edu.vn.gms.repository.ServiceTicketRepository;
import fpt.edu.vn.gms.repository.VehicleRepository;
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

    /**
     * Tạo mới phiếu dịch vụ từ DTO. Nếu chưa có createdAt thì tự động gán thời điểm hiện tại.
     */
    @Override
    public ServiceTicketDto create(ServiceTicketDto dto) {
        ServiceTicket entity = ServiceTicketMapper.mapToServiceTicket(dto);
        if (entity.getCreatedAt() == null) {
            entity.setCreatedAt(LocalDateTime.now());
        }
        ServiceTicket saved = serviceTicketRepository.save(entity);
        return ServiceTicketMapper.mapToServiceTicketDto(saved);
    }

    /**
     * Tạo Phiếu Dịch Vụ
     * @param serviceTicketDto
     * @return
     */
    public ServiceTicketDto createServiceTicket(ServiceTicketDto serviceTicketDto) {
        // Lấy thời điểm hiện tại của hệ thống
        LocalDateTime now = LocalDateTime.now();

        // 1a. Khách mới (chưa có trong hệ thống) – cần thêm thông tin khách và xe để tạo
        // Do ServiceTicketDto hiện không chứa các trường chi tiết khách/xe (fullName, phone, licensePlate, brand, ...),
        // nên không thể tạo mới đầy đủ từ DTO này. Ta báo lỗi hướng dẫn phía frontend truyền đủ dữ liệu qua API phù hợp.
        if (serviceTicketDto.getCustomerId() == null && serviceTicketDto.getVehicleId() == null){

        }
        // Case 1: Khách vãng lai tới trực tiếp
        // 1b. Khách hàng đã tồn tại và đã chọn xe thuộc khách đó (đủ customerId, vehicleId)
        if (serviceTicketDto.getCustomerId() != null && serviceTicketDto.getVehicleId() != null) { // Nếu có sẵn cả mã KH và mã xe
            Customer customerRef = new Customer(); // Tạo đối tượng Customer tạm với id
            customerRef.setCustomerId(serviceTicketDto.getCustomerId()); // Gán id khách hàng
            Vehicle vehicleRef = new Vehicle(); // Tạo đối tượng Vehicle tạm với id
            vehicleRef.setVehicleId(serviceTicketDto.getVehicleId()); // Gán id xe
            // Khởi tạo phiếu dịch vụ
            ServiceTicket st = new ServiceTicket(); // Tạo entity mới
            st.setAppointment(null); // Không gắn lịch hẹn trong trường hợp walk-in
            st.setCustomer(customerRef); // Gán khách hàng đã chọn
            st.setVehicle(vehicleRef);
            st.setCreatedAt(now);
            st.setDeliveryAt(null);
            st.setNotes(serviceTicketDto.getNotes());
            st.setStatus(serviceTicketDto.getStatus() != null ? serviceTicketDto.getStatus() : ServiceTicketStatus.CHO_BAO_GIA);
            // Lưu DB
            ServiceTicket saved = serviceTicketRepository.save(st); // Lưu entity
            // Trả kết quả
            return ServiceTicketMapper.mapToServiceTicketDto(saved); // Trả DTO sau khi lưu
        }

        // Case 2: Tạo từ lịch hẹn (Appointment) khi đã hoàn tất – ưu tiên nếu có appointmentId
        if (serviceTicketDto.getAppointmentId() != null) { // Nếu có truyền mã lịch hẹn
            // Tìm lịch hẹn theo ID (repository dùng Integer nên cần chuyển kiểu)
            Appointment appt = appointmentRepository.findById(Math.toIntExact(serviceTicketDto.getAppointmentId())) // Tìm theo ID
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy Lịch hẹn với id: " + serviceTicketDto.getAppointmentId())); // Nếu không có thì báo lỗi 404

            // (Tùy chọn) Kiểm tra trạng thái lịch hẹn, chỉ cho phép khi trạng thái đã COMPLETED (hoàn tất)
            // Nếu backend không bắt buộc kiểm tra, có thể bỏ qua; ở đây kiểm nhưng không chặn cứng
            if (appt.getStatus() != null && appt.getStatus() != AppointmentStatus.COMPLETED) { // Nếu trạng thái khác COMPLETED
                // Không chặn tạo, nhưng có thể ghi chú; giữ nguyên tiến trình tạo phiếu dịch vụ
            }

            // Lấy thông tin khách hàng và xe từ lịch hẹn
            Customer customer = appt.getCustomer(); // Khách hàng gắn với lịch hẹn
            Vehicle vehicle = appt.getVehicle(); // Xe gắn với lịch hẹn

            // Khởi tạo đối tượng Phiếu Dịch Vụ
            ServiceTicket st = new ServiceTicket(); // Tạo entity mới
            st.setAppointment(appt); // Gán lịch hẹn cho phiếu
            st.setCustomer(customer); // Gán khách hàng cho phiếu
            st.setVehicle(vehicle); // Gán xe cho phiếu
            st.setCreatedAt(now); // created_at: lấy thời gian hệ thống khi tạo
            st.setDeliveryAt(null); // delivery_at: ban đầu để null
            st.setNotes(null); // note: theo yêu cầu để null khi tạo từ lịch hẹn
            st.setStatus(ServiceTicketStatus.CHO_BAO_GIA); // status: mặc định "chờ báo giá"

            // Lưu vào DB
            ServiceTicket saved = serviceTicketRepository.save(st); // Lưu entity và nhận bản ghi đã lưu

            // Trả về DTO cho frontend (bao gồm các thông tin cần thiết)
            return ServiceTicketMapper.mapToServiceTicketDto(saved); // Dùng mapper để trả DTO chuẩn
        }
        throw new IllegalArgumentException(
                "Thiếu dữ liệu để tạo phiếu cho khách mới. Vui lòng sử dụng API chuyên biệt truyền đầy đủ thông tin khách và xe (fullName, phone, licensePlate, ...)."
        ); // Ném lỗi mô tả rõ ràng
    }

}
