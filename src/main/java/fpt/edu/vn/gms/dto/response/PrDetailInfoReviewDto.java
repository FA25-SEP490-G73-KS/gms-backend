package fpt.edu.vn.gms.dto.response;

import fpt.edu.vn.gms.common.enums.PurchaseRequestStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class PrDetailInfoReviewDto {

    private String prCode;
    private PurchaseRequestStatus status;
    private String quotationCode;
    private String licensePlate;
    private String customerName;
    private String customerPhone;
    private List<PurchaseRequestItemResponseDto> items;
}
