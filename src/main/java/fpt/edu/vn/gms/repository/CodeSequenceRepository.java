package fpt.edu.vn.gms.repository;

import fpt.edu.vn.gms.entity.CodeSequence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CodeSequenceRepository extends JpaRepository<CodeSequence, String> {
}
