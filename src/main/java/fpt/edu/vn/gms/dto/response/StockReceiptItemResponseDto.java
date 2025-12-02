package fpt.edu.vn.gms.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;


import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class StockReceiptItemResponseDto {

    private Long receiptItemId;
    private Long receiptId;

    private Long purchaseRequestItemId;
    private String purchaseRequestCode;

    private String sku;
    private Long supplierId;
    private String supplierName;

    @Schema(description = "Số lượng yêu cầu")
    private Double requestedQuantity;

    @Schema(description = "Số lượng đã nhận trong phiếu này")
    private Double quantityReceived;

    @Schema(description = "Tổng số lượng đã nhận từ các phiếu trước đến nay")
    private Double totalQuantityReceived;

    private String attachmentUrl;
    private String note;

    private LocalDateTime receivedAt;

    @Schema(description = "Đơn giá thực tế")
    private BigDecimal actualUnitPrice;

    @Schema(description = "Tổng giá")
    private BigDecimal actualTotalPrice;

    private boolean isPaid;
}
