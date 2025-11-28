package fpt.edu.vn.gms.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PurchaseRequestDetailDto {

    PurchaseRequestResponseDto purchaseRequest;
    List<PurchaseRequestItemResponseDto> items;
}
