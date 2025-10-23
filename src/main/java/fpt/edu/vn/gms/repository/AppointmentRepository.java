package fpt.edu.vn.gms.repository;

import fpt.edu.vn.gms.entity.Appointment;
import fpt.edu.vn.gms.entity.TimeSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    @Query("SELECT a FROM Appointment a " +
            "WHERE a.status = 'CONFIRMED' " +
            "AND a.appointmentDate = :currentDate " +
            "AND a.timeSlot.startTime < :currentTime")
    List<Appointment> findOverdueAppointments(
            @Param("currentDate") LocalDate currentDate,
            @Param("currentTime") LocalTime currentTime);
    int countByAppointmentDateAndTimeSlot(LocalDate appointmentDate, TimeSlot timeSlot);
}
