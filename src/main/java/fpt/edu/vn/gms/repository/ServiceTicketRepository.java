package fpt.edu.vn.gms.repository;

import fpt.edu.vn.gms.entity.ServiceTicket;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository thao tác với bảng Phiếu Dịch Vụ (ServiceTicket).
 * Cung cấp các truy vấn phân trang theo quan hệ: lịch hẹn, khách hàng, xe.
 */
public interface ServiceTicketRepository extends JpaRepository<ServiceTicket, Long> {
}
