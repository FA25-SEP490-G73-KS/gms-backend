package fpt.edu.vn.gms.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class PurchaseRequestDetailDto {

    PurchaseRequestResponseDto purchaseRequest;
    List<PurchaseRequestItemResponseDto> items;
}
