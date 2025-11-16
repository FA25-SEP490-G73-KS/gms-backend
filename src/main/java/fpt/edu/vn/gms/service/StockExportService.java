package fpt.edu.vn.gms.service;


import fpt.edu.vn.gms.dto.response.StockExportItemResponse;
import fpt.edu.vn.gms.dto.response.StockExportResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface StockExportService {

    Page<StockExportResponse> getExportingQuotations(int page, int size);

    List<StockExportItemResponse> getExportingQuotationById(Long quotationId);

    StockExportItemResponse exportItem(Long quotationId, Double exportQty, Long receiverId);
}
