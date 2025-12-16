package fpt.edu.vn.gms.dto.response;

import fpt.edu.vn.gms.common.enums.StockLevelStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PartReqDto {

    @Schema(description = "ID linh kiện", example = "12")
    private Long partId;

    @Schema(description = "Mã Sku")
    private String sku;

    @Schema(description = "Tên linh kiện")
    private String name;

    @Schema(description = "ID danh mục")
    private Long categoryId;

    @Schema(description = "Danh mục")
    private String categoryName;

    @Schema(description = "ID hãng xe")
    private Long brandId;

    @Schema(description = "Hãng xe")
    private String brandName;

    @Schema(description = "ID mẫu xe")
    private Long modelId;

    @Schema(description = "Mẫu xe")
    private String modelName;

    @Schema(description = "ID thị trường")
    private Long marketId;

    @Schema(description = "Thị trường")
    private String marketName;

    @Schema(description = "Có dùng chung không")
    private boolean universal;

    @Schema(description = "ID nhà cung cấp")
    private Long supplierId;

    @Schema(description = "Nhà cung cấp")
    private String supplierName;

    @Schema(description = "Giá nhập")
    private BigDecimal purchasePrice;

    @Schema(description = "Giá bán đã tính auto (mặc định = purchasePrice * 1.10)")
    private BigDecimal sellingPrice;

    @Schema(description = "Số lượng")
    private Double quantity;

    @Schema(description = "ID đơn vị tính")
    private Long unitId;

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

    @Schema(description = "Trạng thái tồn kho (Đủ hàng/Sắp hết/Hết hàng)")
    private StockLevelStatus status;
}
