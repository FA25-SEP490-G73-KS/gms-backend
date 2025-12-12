package fpt.edu.vn.gms.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class PartUpdateReqDto {

    private String name;

    private Long categoryId;

    private Long marketId;

    private Long supplierId;

    private Long vehicleModelId;

    private Long unitId;

    @DecimalMin(value = "0.0", inclusive = false, message = "Giá nhập phải lớn hơn 0")
    private BigDecimal purchasePrice;

    @DecimalMin(value = "0.0", inclusive = false, message = "Giá bán phải lớn hơn 0")
    private BigDecimal sellingPrice;

    private boolean universal;

    private boolean specialPart;

    private Double reorderLevel;

    private String note;
}
