package fpt.edu.vn.gms.dto.request;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

import fpt.edu.vn.gms.common.enums.PriceQuotationStatus;

@Data
@Builder
public class PriceQuotationRequestDto {

    private BigDecimal estimateAmount;
    private BigDecimal discount;

    private List<PriceQuotationItemRequestDto> items;
}
