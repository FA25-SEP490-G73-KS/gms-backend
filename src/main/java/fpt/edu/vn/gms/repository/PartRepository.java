package fpt.edu.vn.gms.repository;

import fpt.edu.vn.gms.common.enums.StockLevelStatus;
import fpt.edu.vn.gms.entity.Part;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PartRepository extends JpaRepository<Part, Long> {

    Page<Part> findByCategory_Id(Long categoryId, Pageable pageable);

    Page<Part> findByStatus(StockLevelStatus status, Pageable pageable);

    Page<Part> findByCategory_IdAndStatus(Long categoryId, StockLevelStatus status, Pageable pageable);
}
