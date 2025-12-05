package fpt.edu.vn.gms.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
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

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    private LocalDateTime createdAt;
    private String status;
}

