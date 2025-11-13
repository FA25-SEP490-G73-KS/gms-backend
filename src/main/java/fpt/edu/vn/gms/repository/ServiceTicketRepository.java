package fpt.edu.vn.gms.repository;

import fpt.edu.vn.gms.common.ServiceTicketStatus;
import fpt.edu.vn.gms.entity.ServiceTicket;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface ServiceTicketRepository extends JpaRepository<ServiceTicket, Long> {

    Page<ServiceTicket> findByStatus(ServiceTicketStatus status, Pageable pageable);

    Page<ServiceTicket> findByCreatedAt(LocalDateTime createdAt, Pageable pageable);

    ServiceTicket findByAppointment_AppointmentId(Long id);
}
