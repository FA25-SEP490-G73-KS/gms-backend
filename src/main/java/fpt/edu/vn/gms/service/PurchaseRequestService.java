package fpt.edu.vn.gms.service;

import fpt.edu.vn.gms.dto.request.PurchaseRequestCreateDto;
import fpt.edu.vn.gms.dto.response.PrDetailInfoReviewDto;
import fpt.edu.vn.gms.dto.response.PurchaseRequestDetailDto;
import fpt.edu.vn.gms.dto.response.PurchaseRequestItemResponseDto;
import fpt.edu.vn.gms.dto.response.PurchaseRequestResponseDto;
import fpt.edu.vn.gms.entity.Employee;
import org.springframework.data.domain.Page;

public interface PurchaseRequestService {

    Page<PurchaseRequestResponseDto> getPurchaseRequests(int page, int size);

    PrDetailInfoReviewDto getPurchaseRequestItems(Long prId);

    PurchaseRequestItemResponseDto getItem(Long itemId);

    PurchaseRequestDetailDto createRequest(PurchaseRequestCreateDto dto);

    PurchaseRequestItemResponseDto reviewItem(Long itemId, boolean approve, String note, Employee currentUser);
}
