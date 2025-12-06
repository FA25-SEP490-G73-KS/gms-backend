package fpt.edu.vn.gms.service.impl;

import fpt.edu.vn.gms.common.enums.PriceQuotationStatus;
import fpt.edu.vn.gms.common.enums.StockLevelStatus;
import fpt.edu.vn.gms.dto.response.MonthWarehouseCostDto;
import fpt.edu.vn.gms.dto.response.PartReqDto;
import fpt.edu.vn.gms.dto.response.TopImportedPartDto;
import fpt.edu.vn.gms.dto.response.WarehouseDashboardResponse;
import fpt.edu.vn.gms.entity.Part;
import fpt.edu.vn.gms.mapper.PartMapper;
import fpt.edu.vn.gms.repository.PartRepository;
import fpt.edu.vn.gms.repository.PriceQuotationRepository;
import fpt.edu.vn.gms.repository.StockExportItemHistoryRepository;
import fpt.edu.vn.gms.repository.StockReceiptItemHistoryRepository;
import fpt.edu.vn.gms.service.WarehouseDashboardService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class WarehouseDashboardServiceImpl implements WarehouseDashboardService {

    PartRepository partRepository;
    PriceQuotationRepository priceQuotationRepository;
    StockReceiptItemHistoryRepository stockReceiptItemHistoryRepository;
    StockExportItemHistoryRepository stockExportItemHistoryRepository;
    PartMapper partMapper;

    @Override
    public WarehouseDashboardResponse getDashboard(Integer year, Integer month) {
        // 1) Counters (luôn là tổng toàn bộ)
        Long totalPartsInStockLong = partRepository.sumAvailableStock();
        long totalPartsInStock = totalPartsInStockLong != null ? totalPartsInStockLong : 0L;

        List<StockLevelStatus> lowStatuses = Arrays.asList(StockLevelStatus.LOW_STOCK, StockLevelStatus.OUT_OF_STOCK);
        long lowStockCount = partRepository.countByStatusIn(lowStatuses);

        BigDecimal totalStockValue = partRepository.sumStockValue();
        if (totalStockValue == null) {
            totalStockValue = BigDecimal.ZERO;
        }

        long pendingQuotationsForWarehouse = priceQuotationRepository.countByStatus(PriceQuotationStatus.WAITING_WAREHOUSE_CONFIRM);

        // 2) Monthly costs - tổng hợp theo năm/tháng (all time)
        List<Object[]> importRows = stockReceiptItemHistoryRepository.getMonthlyImportCostAllTime();
        List<Object[]> exportRows = stockExportItemHistoryRepository.getMonthlyExportCostAllTime();

        Map<String, BigDecimal> importByYearMonth = importRows.stream()
                .collect(Collectors.toMap(
                        r -> ((Number) r[0]).intValue() + "-" + ((Number) r[1]).intValue(),
                        r -> (BigDecimal) r[2]
                ));

        Map<String, BigDecimal> exportByYearMonth = exportRows.stream()
                .collect(Collectors.toMap(
                        r -> ((Number) r[0]).intValue() + "-" + ((Number) r[1]).intValue(),
                        r -> (BigDecimal) r[2]
                ));

        // Hợp nhất các key year-month từ cả import & export
        List<String> yearMonthKeys = new ArrayList<>();
        yearMonthKeys.addAll(importByYearMonth.keySet());
        exportByYearMonth.keySet().stream()
                .filter(k -> !yearMonthKeys.contains(k))
                .forEach(yearMonthKeys::add);

        // Sắp xếp theo năm, tháng
        yearMonthKeys.sort((k1, k2) -> {
            String[] p1 = k1.split("-");
            String[] p2 = k2.split("-");
            int y1 = Integer.parseInt(p1[0]);
            int m1 = Integer.parseInt(p1[1]);
            int y2 = Integer.parseInt(p2[0]);
            int m2 = Integer.parseInt(p2[1]);
            if (y1 != y2) return Integer.compare(y1, y2);
            return Integer.compare(m1, m2);
        });

        List<MonthWarehouseCostDto> monthCosts = new ArrayList<>();
        for (String key : yearMonthKeys) {
            String[] parts = key.split("-");
            int yVal = Integer.parseInt(parts[0]);
            int mVal = Integer.parseInt(parts[1]);
            BigDecimal imp = importByYearMonth.getOrDefault(key, BigDecimal.ZERO);
            BigDecimal exp = exportByYearMonth.getOrDefault(key, BigDecimal.ZERO);
            monthCosts.add(MonthWarehouseCostDto.builder()
                    .year(yVal)
                    .month(mVal)
                    .importCost(imp)
                    .exportCost(exp)
                    .build());
        }

        // 3) Top imported parts - toàn bộ thời gian
        List<Object[]> topRows = stockReceiptItemHistoryRepository.getTopImportedPartsAllTime();
        List<TopImportedPartDto> topImportedParts = new ArrayList<>();
        for (Object[] row : topRows) {
            Long partId = ((Number) row[0]).longValue();
            String partName = (String) row[1];
            String unitName = (String) row[2];
            Double totalQty = ((Number) row[3]).doubleValue();
            topImportedParts.add(TopImportedPartDto.builder()
                    .partId(partId)
                    .partName(partName)
                    .unitName(unitName)
                    .totalImportedQuantity(totalQty)
                    .build());
        }

        // 4) Low stock parts -> PartReqDto
        List<Part> lowStockEntities = partRepository.findByStatusIn(lowStatuses);
        List<PartReqDto> lowStockParts = lowStockEntities.stream()
                .map(partMapper::toDto)
                .collect(Collectors.toList());

        return WarehouseDashboardResponse.builder()
                .totalPartsInStock(totalPartsInStock)
                .lowStockCount(lowStockCount)
                .totalStockValue(totalStockValue)
                .pendingQuotationsForWarehouse(pendingQuotationsForWarehouse)
                .monthCosts(monthCosts)
                .topImportedParts(topImportedParts)
                .lowStockParts(lowStockParts)
                .build();
    }
}
