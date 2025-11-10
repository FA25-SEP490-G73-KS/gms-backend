package fpt.edu.vn.gms.repository;

import fpt.edu.vn.gms.entity.PartCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<PartCategory, Long> {

}
