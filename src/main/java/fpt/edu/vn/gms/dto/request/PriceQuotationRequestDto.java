package fpt.edu.vn.gms.dto.request;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PriceQuotationRequestDto {

    private Long priceQuotationId;
    private List<PriceQuotationItemRequestDto> items;
}
