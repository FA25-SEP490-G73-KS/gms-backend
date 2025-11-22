package fpt.edu.vn.gms.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PartResDto {

    @Schema(description = "Tên linh kiện", example = "Lọc nhớt xe máy")
    @NotBlank(message = "Tên linh kiện không được để trống")
    private String name;

    @Schema(description = "ID danh mục linh kiện", example = "3")
    private Long categoryId;

    @Schema(description = "Id mẫu xe", example = "1")
    private Long vehicleModel;

    @Schema(description = "ID thị trường", example = "1")
    @NotNull(message = "Thị trường không được để trống")
    private Long marketId;

    @Schema(description = "Có dùng chung cho mọi xe không")
    private boolean universal;

    @Schema(description = "Giá nhập linh kiện", example = "100000")
    @NotNull(message = "Giá nhập không được để trống")
    @Positive(message = "Giá nhập phải lớn hơn 0")
    private BigDecimal purchasePrice;

    @Schema(description = "Số lượng tồn kho ban đầu", example = "10")
    @NotNull
    @PositiveOrZero(message = "Số lượng tồn kho phải >= 0")
    private Double quantity;

    @Schema(description = "ID đơn vị tính", example = "2")
    @NotNull(message = "Đơn vị tính không được để trống")
    private Long unitId;

    @Schema(description = "Tỉ lệ giảm giá (nếu có)", example = "5")
    @DecimalMin(value = "0.0", message = "Giảm giá phải >= 0")
    @DecimalMax(value = "100.0", message = "Giảm giá phải <= 100")
    private BigDecimal discountRate;

    @Schema(description = "Có phải phụ tùng đặc biệt không")
    private boolean specialPart;

    @Schema(description = "Ghi chú linh kiện", example = "Hàng chính hãng")
    private String note;
}
