package fpt.edu.vn.gms.dto.request;

import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class PartUpdateReqDto {

    @NotBlank(message = "Tên linh kiện không được để trống")
    private String name;

    @NotNull(message = "Danh mục không được để trống")
    private Long categoryId;

    @NotNull(message = "Thị trường không được để trống")
    private Long marketId;

    @NotNull(message = "Mẫu xe không được để trống")
    private Long vehicleModelId;

    @NotNull(message = "Đơn vị tính không được để trống")
    private Long unitId;

    @NotNull(message = "Giá nhập không được để trống")
    @DecimalMin(value = "0.0", inclusive = false, message = "Giá nhập phải lớn hơn 0")
    private BigDecimal purchasePrice;

    @NotNull(message = "Giá bán không được để trống")
    @DecimalMin(value = "0.0", inclusive = false, message = "Giá bán phải lớn hơn 0")
    private BigDecimal sellingPrice;

    private boolean universal;

    private boolean specialPart;

    private String note;
}
