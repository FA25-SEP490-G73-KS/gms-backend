package fpt.edu.vn.gms.dto.response;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseRequestDetailDto {

    @Schema(example = "1")
    private Long id;

    @Schema(example = "PR-2025-00001")
    private String code;

    @Schema(example = "Từ báo giá")
    private String reason;

    @Schema(example = "BG-2025-00001")
    private String quotationCode;

    @Schema(example = "Nguyễn Văn A")
    private String customerName;

    @Schema(example = "Nam")
    private String createdBy;

    @Schema(example = "11/12/2025 15:05")
    private String createdAt;

    @Schema(example = "Chờ duyệt")
    private String reviewStatus;

    @ArraySchema(arraySchema = @Schema(description = "Danh sách item của phiếu yêu cầu mua hàng"))
    private List<PurchaseRequestItemDto> items;
}

