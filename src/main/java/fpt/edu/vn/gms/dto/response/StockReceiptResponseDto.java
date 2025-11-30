package fpt.edu.vn.gms.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class StockReceiptResponseDto {

    private Long receiptId;
    private String code;

    private String vehiclePlate;
    private String vehicleModelName;

    private String createdByName;
    private LocalDateTime createdAt;

    private BigDecimal totalAmount;
    private String status; // CREATED / COMPLETED...
}
