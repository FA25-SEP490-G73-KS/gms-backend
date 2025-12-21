package fpt.edu.vn.gms.repository;

import fpt.edu.vn.gms.common.enums.AppointmentStatus;
import fpt.edu.vn.gms.entity.Appointment;
import fpt.edu.vn.gms.entity.Customer;
import fpt.edu.vn.gms.entity.TimeSlot;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    @Query("SELECT a FROM Appointment a JOIN FETCH a.timeSlot WHERE a.appointmentDate = :date")
    List<Appointment> findByAppointmentDate(@Param("date") LocalDate date);

    Page<Appointment> getByStatus(AppointmentStatus status, Pageable pageable);

    @Query("""
                SELECT COUNT(a)
                FROM Appointment a
                WHERE DATE(a.appointmentDate) = :date
            """)
    long countByDate(@Param("date") LocalDate date);

    int countByAppointmentDateAndTimeSlot(LocalDate appointmentDate, TimeSlot timeSlot);

    int countByCustomerAndAppointmentDate(Customer customer, LocalDate appointmentDate);

    List<Appointment> findByAppointmentDateAndStatus(LocalDate appointmentDate, AppointmentStatus status);

    @Query("SELECT a FROM Appointment a WHERE a.appointmentDate = :appointmentDate " +
            "AND a.status = :status AND a.isReminderSent = false")
    List<Appointment> findByAppointmentDateAndStatusAndReminderSentFalse(
            @Param("appointmentDate") LocalDate appointmentDate,
            @Param("status") AppointmentStatus status);

    List<Appointment> findByStatusAndConfirmedAtIsNullAndAppointmentDate(
            AppointmentStatus status,
            LocalDate date);

    long countByAppointmentDate(LocalDate appointmentDate);

    // NEW: find by unique appointmentCode
    Optional<Appointment> findByAppointmentCode(String appointmentCode);
}