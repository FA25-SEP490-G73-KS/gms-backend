package fpt.edu.vn.gms.dto.request;

import fpt.edu.vn.gms.common.Market;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Set;

@Data
@Builder
public class PartReqDto {

    @NotBlank
    private String name;
    private Long categoryId;
    private Set<Long> compatibleVehicleModelIds;
    private Market market;
    private boolean isUniversal;
    private BigDecimal purchasePrice;
    private BigDecimal sellingPrice;
    private BigDecimal discountRate;
    private String unit;
    private boolean specialPart;
    private Double reorderLevel;
}
