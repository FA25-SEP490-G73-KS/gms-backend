package fpt.edu.vn.gms.dto.response;

import fpt.edu.vn.gms.entity.PurchaseRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PurchaseRequestResponseDto {

    @Schema(example = "1")
    private Long id;

    @Schema(example = "PR-2025-00001")
    private String code;

    private Long quotationId;

    @Schema(example = "BG-2025-00001")
    private String quotationCode;

    @Schema(example = "15000000")
    private BigDecimal totalEstimatedAmount;

    @Schema(example = "Chờ duyệt")
    private String reviewStatus;

    private String reason;

    @Schema(example = "11/12/2025 15:05")
    private String createdAt;

    public static PurchaseRequestResponseDto fromEntity(PurchaseRequest pr) {
        if (pr == null) return null;
        return PurchaseRequestResponseDto.builder()
                .id(pr.getId())
                .code(pr.getCode())
                .quotationId(pr.getRelatedQuotation() != null ? pr.getRelatedQuotation().getPriceQuotationId() : null)
                .quotationCode(pr.getRelatedQuotation() != null ? pr.getRelatedQuotation().getCode() : null)
                .totalEstimatedAmount(pr.getTotalEstimatedAmount())
                .reviewStatus(pr.getReviewStatus() != null ? pr.getReviewStatus().name() : null)
                .reason(pr.getReason())
                .createdAt(pr.getCreatedAt() != null ? pr.getCreatedAt().toString() : null)
                .build();
    }
}
