package fpt.edu.vn.gms.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class ManualVoucherResponseDto {

    @Schema(description = "ID phiếu")
    private Long id;

    @Schema(description = "Mã phiếu")
    private String code;

    @Schema(description = "Loại phiếu")
    private String type; // RECEIPT, PAYMENT

    @Schema(description = "Tên đối tượng thụ hưởng / chi trả")
    private String targetName;

    @Schema(description = "Số tiền")
    private BigDecimal amount;

    @Schema(description = "Danh mục")
    private String category;

    @Schema(description = "Nội dung phiếu")
    private String description;

    @Schema(description = "Ngày tạo phiếu")
    private LocalDateTime createdAt;

    @Schema(description = "Ngày quản lý duyệt")
    private LocalDateTime approvedAt;

    @Schema(description = "Người lập phiếu")
    private String createdBy;

    @Schema(description = "Người duyệt phiếu")
    private String approvedBy;

    @Schema(description = "Trạng thái phiếu")
    private String status; // COMPLETED / ...

    @Schema(description = "File đính kèm")
    private String attachmentUrl;
}
