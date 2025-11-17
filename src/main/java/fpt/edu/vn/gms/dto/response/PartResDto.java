package fpt.edu.vn.gms.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Set;

import fpt.edu.vn.gms.common.enums.Market;

@Data
@Builder
public class PartResDto {

    private Long partId;
    private String name;
    private Long categoryId;
    private String categoryName;
    private Set<Long> compatibleVehicleIds; // chỉ lấy id để đơn giản
    private Market market;
    private boolean isUniversal;
    private BigDecimal purchasePrice;
    private BigDecimal sellingPrice;
    private BigDecimal discountRate;
    private Double quantityInStock;
    private String unit;
    private Double reservedQuantity;
    private Double reorderLevel;
    private boolean specialPart;
}
