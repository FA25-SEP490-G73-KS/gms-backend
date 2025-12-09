package fpt.edu.vn.gms.service;

import fpt.edu.vn.gms.dto.response.PurchaseRequestDetailDto;
import fpt.edu.vn.gms.dto.response.PurchaseRequestResponseDto;
import fpt.edu.vn.gms.entity.PurchaseRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PurchaseRequestService {

    PurchaseRequest createPurchaseRequestFromQuotation(Long quotationId);

    PurchaseRequest approvePurchaseRequest(Long requestId);

    PurchaseRequest rejectPurchaseRequest(Long requestId, String reason);

    Page<PurchaseRequestResponseDto> getPurchaseRequests(String keyword, String status, String fromDate, String toDate,
            Pageable pageable);

    PurchaseRequestDetailDto getPurchaseRequestDetail(Long id);
}
