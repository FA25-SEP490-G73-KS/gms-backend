package fpt.edu.vn.gms.repository;

import fpt.edu.vn.gms.entity.Appointment;
import fpt.edu.vn.gms.entity.TimeSlot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;

public interface AppointmentRepository extends JpaRepository<Appointment, Integer> {

    int countByAppointmentDateAndTimeSlot(LocalDate appointmentDate, TimeSlot timeSlot);
}
