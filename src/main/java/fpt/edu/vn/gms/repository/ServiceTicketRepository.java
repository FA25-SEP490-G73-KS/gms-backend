package fpt.edu.vn.gms.repository;

import fpt.edu.vn.gms.common.enums.ServiceTicketStatus;
import fpt.edu.vn.gms.dto.response.CustomerServiceHistoryDto;
import fpt.edu.vn.gms.entity.ServiceTicket;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ServiceTicketRepository extends JpaRepository<ServiceTicket, Long> {

    Page<ServiceTicket> findByStatus(ServiceTicketStatus status, Pageable pageable);

    Page<ServiceTicket> findByCreatedAt(LocalDateTime createdAt, Pageable pageable);

    @Query("""
        SELECT st
        FROM ServiceTicket st
        WHERE (:status IS NULL OR st.status = :status)
          AND (:from IS NULL OR st.createdAt >= :from)
          AND (:to   IS NULL OR st.createdAt <= :to)
        ORDER BY st.createdAt DESC
    """)
    Page<ServiceTicket> searchByStatusAndCreatedAt(
            @Param("status") ServiceTicketStatus status,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to,
            Pageable pageable
    );

    ServiceTicket findByAppointment_AppointmentId(Long id);

    @Query("""
    SELECT COUNT(st)
    FROM ServiceTicket st
    WHERE DATE(st.createdAt) = :date
""")
    long countByDate(@Param("date") LocalDate date);

    @Query("""
        SELECT YEAR(st.createdAt) as year, MONTH(st.createdAt) as month, COUNT(st) as count
        FROM ServiceTicket st
        WHERE st.status = :status
        GROUP BY YEAR(st.createdAt), MONTH(st.createdAt)
        ORDER BY YEAR(st.createdAt), MONTH(st.createdAt)
    """)
    List<Object[]> countCompletedTicketsGroupedByMonth(@Param("status") String status);

    @Query("""
        SELECT t.name, COUNT(st)
        FROM ServiceTicket st JOIN st.serviceTypes t
        WHERE YEAR(st.createdAt) = :year AND MONTH(st.createdAt) = :month
              AND st.status = 'COMPLETED'
        GROUP BY t.name
    """)
    List<Object[]> countTicketsByTypeForMonth(@Param("year") int year, @Param("month") int month);

    @Query("""
        SELECT new fpt.edu.vn.gms.dto.response.CustomerServiceHistoryDto(
            st.serviceTicketId,
            st.serviceTicketCode,
            v.licensePlate,
            st.createdAt,
            st.deliveryAt,
            pq.estimateAmount,
            st.status
        )
        FROM ServiceTicket st
        JOIN st.vehicle v
        LEFT JOIN PriceQuotation pq ON pq.serviceTicket.serviceTicketId = st.serviceTicketId
        WHERE st.customer.customerId = :customerId
        ORDER BY st.createdAt DESC
    """)
    List<CustomerServiceHistoryDto> getCustomerServiceHistory(Long customerId);

    @Query("""
       SELECT st FROM ServiceTicket st
       JOIN FETCH st.customer
       JOIN FETCH st.vehicle
       WHERE st.serviceTicketId = :id
       """)
    Optional<ServiceTicket> findDetail(@Param("id") Long id);

    // Dashboard: tổng số phiếu dịch vụ trong tháng hiện tại
    @Query("""
        SELECT COUNT(st)
        FROM ServiceTicket st
        WHERE YEAR(st.createdAt) = :year AND MONTH(st.createdAt) = :month
    """)
    long countServiceTicketsInMonth(@Param("year") int year, @Param("month") int month);

    // Dashboard: số xe đang sửa (IN_PROGRESS)
    long countByStatus(ServiceTicketStatus status);

    // Dashboard: thống kê số ServiceTicket group by YEAR, MONTH
    @Query("""
        SELECT YEAR(st.createdAt) as year, MONTH(st.createdAt) as month, COUNT(st) as total
        FROM ServiceTicket st
        GROUP BY YEAR(st.createdAt), MONTH(st.createdAt)
        ORDER BY YEAR(st.createdAt), MONTH(st.createdAt)
    """)
    List<Object[]> getTicketsByMonth();
}
