package fpt.edu.vn.gms.repository;

import fpt.edu.vn.gms.common.enums.StockLevelStatus;
import fpt.edu.vn.gms.entity.Part;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.util.List;

public interface PartRepository extends JpaRepository<Part, Long> {

    Page<Part> findByStatus(StockLevelStatus status, Pageable pageable);

    Page<Part> findByCategory_IdAndStatus(Long categoryId, StockLevelStatus status, Pageable pageable);

    // New: filter by category only with pagination
    Page<Part> findByCategory_Id(Long categoryId, Pageable pageable);

    // Warehouse dashboard: tổng tồn khả dụng = SUM(quantityInStock - reservedQuantity)
    @Query("""
        SELECT COALESCE(SUM(p.quantityInStock - p.reservedQuantity), 0)
        FROM Part p
    """)
    Long sumAvailableStock();

    // Warehouse dashboard: tổng giá trị tồn kho
    @Query("""
        SELECT COALESCE(SUM((p.quantityInStock - p.reservedQuantity) * p.purchasePrice), 0)
        FROM Part p
    """)
    BigDecimal sumStockValue();

    // Đếm số linh kiện có trạng thái tồn kho thuộc danh sách cho trước
    long countByStatusIn(List<StockLevelStatus> statuses);

    // Lấy danh sách linh kiện theo nhiều trạng thái tồn kho (dùng cho low stock list)
    List<Part> findByStatusIn(List<StockLevelStatus> statuses);
}
