package fpt.edu.vn.gms.dto.request;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateReceiptItemHistoryRequest {

    private Double quantity;
    private BigDecimal unitPrice;
    private String attachmentUrl;
    private String note;
    private String receivedBy;
}

