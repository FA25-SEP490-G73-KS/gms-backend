package fpt.edu.vn.gms.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class StockReceiptListResponse {

    private Long id;
    private String code;
    private String supplierName;
    private String purchaseRequestCode;
    private Long lineCount;
    private Double receivedQty;
    private Double totalQty;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    private LocalDateTime createdAt;
    private String status;
}

