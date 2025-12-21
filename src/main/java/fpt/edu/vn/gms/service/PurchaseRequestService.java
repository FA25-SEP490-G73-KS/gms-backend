package fpt.edu.vn.gms.service;

import fpt.edu.vn.gms.dto.request.CreatePurchaseRequestFromQuotationDto;
import fpt.edu.vn.gms.dto.request.PurchaseRequestCreateDto;
import fpt.edu.vn.gms.dto.response.PriceQuotationItemResponseDto;
import fpt.edu.vn.gms.dto.response.PurchaseRequestDetailDto;
import fpt.edu.vn.gms.dto.response.PurchaseRequestResponseDto;
import fpt.edu.vn.gms.dto.response.PurchaseSuggestionItemDto;
import fpt.edu.vn.gms.entity.Employee;
import fpt.edu.vn.gms.entity.PurchaseRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PurchaseRequestService {

    PurchaseRequest createPurchaseRequestFromQuotation(Long quotationId);

    /**
     * Tạo phiếu yêu cầu mua hàng thủ công từ danh sách linh kiện do người dùng
     * chọn.
     */
    PurchaseRequest createRequest(PurchaseRequestCreateDto dto);

    /**
     * Lấy danh sách linh kiện đang thiếu để hiển thị trên modal tạo phiếu mua hàng.
     */
    List<PurchaseSuggestionItemDto> getSuggestedPurchaseItems();

    PurchaseRequest approvePurchaseRequest(Long requestId);

    PurchaseRequest rejectPurchaseRequest(Long requestId, String reason);

    Page<PurchaseRequestResponseDto> getPurchaseRequests(String keyword, String status, String fromDate, String toDate,
            Pageable pageable);

    PurchaseRequestDetailDto getPurchaseRequestDetail(Long id);

    PurchaseRequest createFromQuotation(CreatePurchaseRequestFromQuotationDto dto, Employee currentEmployee);

    List<PriceQuotationItemResponseDto> getQuotationItemsByPurchaseRequest(Long purchaseRequestId);
}
