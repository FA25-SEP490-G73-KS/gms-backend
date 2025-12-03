package fpt.edu.vn.gms.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ManualTransactionResponse {
    private Long id;
    private String code;
    private String type;
    private Boolean isDraft;
    private String status;
    private String createdBy;
    private LocalDateTime createdAt;
    private String note;
    private BigDecimal totalAmount;
    private List<ManualTransactionItemResponse> items;
}

