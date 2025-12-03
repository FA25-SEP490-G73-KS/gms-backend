package fpt.edu.vn.gms.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class StockReceiptDetailResponse {

    private Long id;
    private String code;
    private String supplierName;
    private String purchaseRequestCode;

    private String createdBy;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    private LocalDateTime createdAt;

    private String receivedBy;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    private LocalDateTime receivedAt;

    private String status;
    private BigDecimal totalAmount;
    private String note;

    private List<StockReceiptItemResponse> items;
}

