package fpt.edu.vn.gms.service;

import fpt.edu.vn.gms.dto.response.PurchaseRequestItemDetailDto;

import java.util.List;

public interface PurchaseRequestItemService {

    List<PurchaseRequestItemDetailDto> getItemsByPurchaseRequestId(Long purchaseRequestId);
}
