package fpt.edu.vn.gms.service;

import fpt.edu.vn.gms.dto.response.PurchaseRequestItemResponseDto;
import fpt.edu.vn.gms.dto.response.PurchaseRequestResponseDto;
import org.springframework.data.domain.Page;

import java.util.List;

public interface PurchaseRequestService {

    Page<PurchaseRequestResponseDto> getPurchaseRequests(int page, int size);

    List<PurchaseRequestItemResponseDto> getPurchaseRequestItems(Long prId);

    PurchaseRequestItemResponseDto confirmPurchaseRequestItem(Long itemId, boolean approved, String note);
}
