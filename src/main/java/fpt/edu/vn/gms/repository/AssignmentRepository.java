package fpt.edu.vn.gms.repository;

import fpt.edu.vn.gms.entity.Appointment;
import org.hibernate.sql.ast.tree.update.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssignmentRepository extends JpaRepository<Assignment, Integer> {
}
