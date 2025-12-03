package fpt.edu.vn.gms.repository;

import fpt.edu.vn.gms.common.enums.ExportItemStatus;
import fpt.edu.vn.gms.entity.Part;
import fpt.edu.vn.gms.entity.StockExportItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StockExportItemRepository extends JpaRepository<StockExportItem, Long> {
    List<StockExportItem> findByPartAndStatus(Part part, ExportItemStatus status);
}
