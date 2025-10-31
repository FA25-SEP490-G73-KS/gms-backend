package fpt.edu.vn.gms.dto.request;

import fpt.edu.vn.gms.common.PriceQuotationStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class PriceQuotationRequestDto {

    private Long priceQuotationId;
    private BigDecimal estimateAmount;
    private BigDecimal discount;

    private List<PriceQuotationItemRequestDto> items;
}
