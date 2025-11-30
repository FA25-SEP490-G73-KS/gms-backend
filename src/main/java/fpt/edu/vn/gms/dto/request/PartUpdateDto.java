package fpt.edu.vn.gms.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PartUpdateDto {

    @NotNull
    @Positive
    private BigDecimal purchasePrice;

    @NotNull
    @Positive
    private BigDecimal sellingPrice;

    private String warehouseNote;
}
