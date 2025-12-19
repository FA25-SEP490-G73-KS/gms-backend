package fpt.edu.vn.gms.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Dto hiển thị danh sách linh kiện cần mua cho modal tạo phiếu yêu cầu mua
 * hàng.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PurchaseSuggestionItemDto {

    private Long partId;

    private String sku;

    private String partName;

    private String unit;

    private Double quantityInStock;

    private Double reservedQuantity;

    private Double reorderLevel;

    /**
     * Tồn khả dụng = quantityInStock - reservedQuantity.
     */
    private Double available;

    /**
     * Số lượng đề xuất mua thêm để đạt ngưỡng reorderLevel.
     */
    private Double suggestedQuantity;
}
