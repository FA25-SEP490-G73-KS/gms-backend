package fpt.edu.vn.gms.repository;

import fpt.edu.vn.gms.entity.TimeSlot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TimeSlotRepository extends JpaRepository<TimeSlot, Integer> {
    Optional<TimeSlot> findByTimeSlotId(Long id);
}
