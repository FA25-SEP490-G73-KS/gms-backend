package fpt.edu.vn.gms.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

import fpt.edu.vn.gms.common.enums.ExportStatus;

@Data
@Builder
public class StockExportResponse {

    private Long priceQuotationId;
    private String priceQuotationCode;
    private String customerName;
    private String licensePlate;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDateTime createdAt;
    private ExportStatus exportStatus;
}
