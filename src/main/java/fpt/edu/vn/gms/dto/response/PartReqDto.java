package fpt.edu.vn.gms.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class PartReqDto {

    @Schema(description = "ID linh kiện", example = "12")
    private Long partId;

    @Schema(description = "Mã Sku")
    private String sku;

    @Schema(description = "Tên linh kiện")
    private String name;

    @Schema(description = "Danh mục")
    private String categoryName;

    @Schema(description = "Mẫu xe")
    private String modelName;

    @Schema(description = "Thị trường")
    private String marketName;

    @Schema(description = "Có dùng chung không")
    private boolean universal;

    @Schema(description = "Nhà cung cấp")
    private String supplierName;

    @Schema(description = "Giá nhập")
    private BigDecimal purchasePrice;

    @Schema(description = "Giá bán đã tính auto (mặc định = purchasePrice * 1.10)")
    private BigDecimal sellingPrice;

    @Schema(description = "Số lượng")
    private Double quantity;

    @Schema(description = "Đơn vị tính")
    private String unitName;

    @Schema(description = "Số lượng đang giữ")
    private Double reservedQuantity;

    @Schema(description = "Số lượng tồn tối thiểu trong khô")
    private Double reorderLevel;

    @Schema(description = "Tỉ lệ giảm giá")
    private BigDecimal discountRate;

    @Schema(description = "Phụ tùng đặc biệt")
    private boolean specialPart;

    @Schema(description = "Ghi chú")
    private String note;
}
