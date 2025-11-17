package fpt.edu.vn.gms.repository;

import fpt.edu.vn.gms.common.enums.ServiceTicketStatus;
import fpt.edu.vn.gms.entity.ServiceTicket;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface ServiceTicketRepository extends JpaRepository<ServiceTicket, Long> {

    Page<ServiceTicket> findByStatus(ServiceTicketStatus status, Pageable pageable);

    Page<ServiceTicket> findByCreatedAt(LocalDateTime createdAt, Pageable pageable);

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


}
