package fpt.edu.vn.gms.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PurchaseRequestItemDto {

    @Schema(example = "LOC-GIO-TOYOTA-CAMRY-2019")
    private String sku;

    @Schema(example = "Lọc gió động cơ")
    private String partName;

    @Schema(example = "10")
    private Double quantity;

    @Schema(example = "Cái")
    private String unit;

    @Schema(example = "150000")
    private Long estimatedPurchasePrice;

    @Schema(example = "1500000")
    private Long total;
}

