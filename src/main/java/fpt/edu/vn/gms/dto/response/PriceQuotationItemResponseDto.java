package fpt.edu.vn.gms.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class PriceQuotationItemResponseDto {

    private Long id;
    private String partName;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal totalPrice;
    private String status;
}
