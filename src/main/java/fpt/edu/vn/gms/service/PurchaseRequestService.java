package fpt.edu.vn.gms.service;

import fpt.edu.vn.gms.dto.response.PurchaseRequestResponseDto;
import org.springframework.data.domain.Page;

public interface PurchaseRequestService {

    Page<PurchaseRequestResponseDto> getAllRequests(int page, int size);

    void approveRequestItem(Long id, Long itemId);

    void rejectRequestItem(Long id, Long itemId, String reason);

//    PurchaseRequestResponseDto getPurchaseRequestById(Long id);
}
