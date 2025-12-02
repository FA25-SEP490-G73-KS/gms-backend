package fpt.edu.vn.gms.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class StockExportListResponse {

    private Long id;
    private String code;
    private String reason;
    private String customerName;
    private String quotationCode;
    private String createdBy;
    private LocalDateTime createdAt;
    private String status;
}

