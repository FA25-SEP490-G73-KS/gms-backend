package fpt.edu.vn.gms.service;

import fpt.edu.vn.gms.dto.request.ExportItemRequest;
import fpt.edu.vn.gms.dto.response.*;
import fpt.edu.vn.gms.entity.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface StockExportService {

    StockExportDetailResponse createExportFromQuotation(Long quotationId, String reason, Employee creator);

    Page<StockExportListResponse> getExports(String keyword, String status, String fromDate, String toDate, Pageable pageable);

    StockExportDetailResponse getExportDetail(Long id);

    Page<StockExportItemResponse> getExportItems(Long exportId, Pageable pageable);

    ExportItemDetailResponse getExportItemDetail(Long itemId);

    StockExportItemResponse exportItem(Long itemId, ExportItemRequest request, Employee exportedBy);

    ExportItemDetailResponse getExportItemHistory(Long itemId);

    StockExportDetailResponse approveExport(Long id, Employee approver);

    StockExportDetailResponse cancelExport(Long id, Employee canceller);

    StockExportDetailResponse markCompleted(Long id, Employee employee);
}

