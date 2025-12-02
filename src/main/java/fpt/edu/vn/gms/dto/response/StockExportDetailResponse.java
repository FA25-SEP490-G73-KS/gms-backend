package fpt.edu.vn.gms.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class StockExportDetailResponse {

    private Long id;
    private String code;
    private String reason;
    private String status;

    private Long quotationId;
    private String quotationCode;
    private Long serviceTicketId;
    private String customerName;

    private String createdBy;
    private String approvedBy;
    private String exportedBy;

    private LocalDateTime createdAt;
    private LocalDateTime approvedAt;
    private LocalDateTime exportedAt;

    private List<StockExportItemResponse> items;
}
