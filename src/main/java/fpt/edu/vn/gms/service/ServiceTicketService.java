package fpt.edu.vn.gms.service;

import fpt.edu.vn.gms.dto.request.ServiceTicketRequestDto;
import fpt.edu.vn.gms.dto.response.ServiceTicketResponseDto;
import org.springframework.data.domain.Page;

public interface ServiceTicketService {

    ServiceTicketResponseDto createServiceTicket(ServiceTicketRequestDto req);

    /**
     * Lấy chi tiết phiếu dịch vụ theo ID.
     *
     * @param serviceTicketId mã phiếu dịch vụ
     * @return thông tin phiếu dịch vụ
     */
    ServiceTicketResponseDto getServiceTicketById(Long serviceTicketId);

    /**
     * Lấy danh sách phiếu dịch vụ có phân trang.
     *
     * @param page thông tin phân trang và sắp xếp
     * @param size kích thước trang
     * @return trang dữ liệu các phiếu dịch vụ
     */
    Page<ServiceTicketResponseDto> getAllServiceTicket(int page, int size);

    /**
     * Cập nhật phiếu dịch vụ theo ID.
     *
     * @param serviceTicketId mã phiếu dịch vụ cần cập nhật
     * @param dto             dữ liệu cập nhật
     * @return phiếu dịch vụ sau khi cập nhật
     */
    ServiceTicketResponseDto updateServiceTicket(Long serviceTicketId, ServiceTicketRequestDto dto);

    ServiceTicketResponseDto createServiceTicketFromAppointment(Long appointmentId, ServiceTicketRequestDto dto);

}
