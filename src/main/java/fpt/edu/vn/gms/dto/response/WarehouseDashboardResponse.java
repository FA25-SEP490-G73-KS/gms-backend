package fpt.edu.vn.gms.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WarehouseDashboardResponse {

    private long totalPartsInStock;
    private long lowStockCount;
    private BigDecimal totalStockValue;
    private long pendingQuotationsForWarehouse;
    private List<MonthWarehouseCostDto> monthCosts;
    private List<TopImportedPartDto> topImportedParts;
    private List<PartReqDto> lowStockParts;
}
