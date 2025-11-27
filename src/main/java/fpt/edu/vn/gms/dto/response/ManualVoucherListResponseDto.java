package fpt.edu.vn.gms.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class ManualVoucherListResponseDto {

    @Schema(description = "ID phiếu")
    private Long id;

    @Schema(description = "Mã phiếu")
    private String code;

    @Schema(description = "Loại phiếu")
    private String type;

    @Schema(description = "Đối tượng", name = "targetName")
    private String targetName;

    @Schema(description = "Số tiền")
    private BigDecimal amount;

    @Schema(description = "Ngày tạo")
    private LocalDateTime createdAt;

    @Schema(description = "Trạng thái")
    private String status;
}
