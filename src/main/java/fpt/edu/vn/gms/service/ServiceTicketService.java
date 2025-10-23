package fpt.edu.vn.gms.service;

import fpt.edu.vn.gms.dto.ServiceTicketDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Dịch vụ xử lý Phiếu Dịch Vụ (ServiceTicket).
 * Cung cấp các thao tác CRUD cơ bản và tìm kiếm có phân trang/sắp xếp.
 */
public interface ServiceTicketService {
    /**
     * Tạo mới một phiếu dịch vụ.
     *
     * @param dto dữ liệu phiếu dịch vụ cần tạo
     * @return phiếu dịch vụ đã tạo (kèm ID)
     */
    ServiceTicketDto create(ServiceTicketDto dto);

    /**
     * Tạo mới một phiếu dịch vụ.
     *
     * @param req dữ liệu phiếu dịch vụ cần tạo
     * @return phiếu dịch vụ đã tạo
     */
    ServiceTicketDto createNewServiceTicket(ServiceTicketDto req);

    /**
     * Lấy chi tiết phiếu dịch vụ theo ID.
     *
     * @param id mã phiếu dịch vụ
     * @return thông tin phiếu dịch vụ
     */
    ServiceTicketDto getById(Long id);

    /**
     * Lấy danh sách phiếu dịch vụ có phân trang.
     *
     * @param pageable thông tin phân trang và sắp xếp
     * @return trang dữ liệu các phiếu dịch vụ
     */
    Page<ServiceTicketDto> getAll(Pageable pageable);

    /**
     * Cập nhật phiếu dịch vụ theo ID.
     * Chỉ các trường có giá trị (khác null) trong DTO mới được áp dụng cập nhật.
     *
     * @param id  mã phiếu dịch vụ cần cập nhật
     * @param dto dữ liệu cập nhật
     * @return phiếu dịch vụ sau khi cập nhật
     */
    ServiceTicketDto update(Long id, ServiceTicketDto dto);


}
