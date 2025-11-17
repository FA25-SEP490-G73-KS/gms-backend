package fpt.edu.vn.gms.dto.request;

import fpt.edu.vn.gms.common.enums.PriceQuotationStatus;
import fpt.edu.vn.gms.entity.PriceQuotation;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChangeQuotationStatusReqDto {

    private PriceQuotationStatus status;
}
