package fpt.edu.vn.gms.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
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

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    private LocalDateTime approvedAt;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm")
    private LocalDateTime exportedAt;

    private List<StockExportItemResponse> items;
}
