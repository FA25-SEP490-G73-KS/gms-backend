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
     * @param dto dữ liệu phiếu dịch vụ cần tạo
     * @return phiếu dịch vụ đã tạo (kèm ID)
     */
    ServiceTicketDto create(ServiceTicketDto dto);

    /**
     *  Tạo mới một phiếu dịch vụ.
     * @param serviceTicketDto dữ liệu phiếu dịch vụ cần tạo
     * @return phiếu dịch vụ đã tạo (kèm ID)
     */
    public ServiceTicketDto createServiceTicket(ServiceTicketDto serviceTicketDto);
}
