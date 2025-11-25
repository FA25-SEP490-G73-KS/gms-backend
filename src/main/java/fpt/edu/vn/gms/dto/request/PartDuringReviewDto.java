package fpt.edu.vn.gms.dto.request;

import lombok.Data;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

@Data
public class PartDuringReviewDto {

    @NotNull
    private Long categoryId;

    @NotNull
    private Long marketId;

    @NotBlank
    private String name;

    @NotBlank
    private String note;

    @NotNull
    @Positive
    private BigDecimal purchasePrice;

    @NotNull
    private Boolean specialPart;

    @NotNull
    private Long unitId;

    @NotNull
    private Boolean universal;

    @NotNull
    private Long supplierId;

    @NotNull
    private Long vehicleModelId;

    private String warehouseNote;
}
