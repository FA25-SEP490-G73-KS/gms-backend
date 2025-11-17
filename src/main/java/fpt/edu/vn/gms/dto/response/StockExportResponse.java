package fpt.edu.vn.gms.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

import fpt.edu.vn.gms.common.enums.ExportStatus;

@Data
@Builder
public class StockExportResponse {

    private Long priceQuotationId;
    private String customerName;
    private String licensePlate;
    private LocalDateTime createdAt;
    private ExportStatus exportStatus;
}
