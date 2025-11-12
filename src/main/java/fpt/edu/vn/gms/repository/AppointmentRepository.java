package fpt.edu.vn.gms.repository;

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
import java.time.LocalTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    @Query("SELECT a FROM Appointment a " +
            "WHERE a.status = 'PENDING' " +
            "AND a.appointmentDate = :currentDate " +
            "AND a.timeSlot.endTime < :currentTime")
    List<Appointment> findOverdueAppointments(
            @Param("currentDate") LocalDate currentDate,
            @Param("currentTime") LocalTime currentTime);

    Page<Appointment> findByAppointmentDate(LocalDate appointmentDate, Pageable pageable);

    int countByAppointmentDateAndTimeSlot(LocalDate appointmentDate, TimeSlot timeSlot);

    int countByCustomerAndAppointmentDate(Customer customer, LocalDate appointmentDate);
}