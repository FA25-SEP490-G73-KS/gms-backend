package fpt.edu.vn.gms.dto.response;

import fpt.edu.vn.gms.common.ExportStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class StockExportResponse {

    private Long priceQuotationId;
    private String customerName;
    private String licensePlate;
    private LocalDateTime createdAt;
    private ExportStatus exportStatus;
}
