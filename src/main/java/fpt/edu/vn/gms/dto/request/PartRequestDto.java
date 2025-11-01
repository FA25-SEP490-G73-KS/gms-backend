package fpt.edu.vn.gms.dto.request;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Set;

@Data
@Builder
public class PartRequestDto {

    private String partName;
    private String origin;
    private String brand;
    private Integer quantity;
    private BigDecimal importPrice;
    private BigDecimal salePrice;
    private Set<Long> vehicleModelIds;
    private Boolean isUniversal;
}
