package fpt.edu.vn.gms.controller;

import fpt.edu.vn.gms.common.annotations.CurrentUser;
import fpt.edu.vn.gms.dto.request.CreatePurchaseRequestFromQuotationDto;
import fpt.edu.vn.gms.dto.request.PurchaseRequestCreateDto;
import fpt.edu.vn.gms.dto.response.ApiResponse;
import fpt.edu.vn.gms.dto.response.PriceQuotationItemResponseDto;
import fpt.edu.vn.gms.dto.response.PurchaseRequestDetailDto;
import fpt.edu.vn.gms.dto.response.PurchaseRequestResponseDto;
import fpt.edu.vn.gms.dto.response.PurchaseSuggestionItemDto;
import fpt.edu.vn.gms.entity.Employee;
import fpt.edu.vn.gms.entity.PurchaseRequest;
import fpt.edu.vn.gms.service.PurchaseRequestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/purchase-requests")
@RequiredArgsConstructor
@Tag(name = "purchase-request-controller", description = "Tạo và duyệt phiếu yêu cầu mua hàng")
public class PurchaseRequestControllerNew {

    private final PurchaseRequestService purchaseRequestService;

    @PostMapping("/from-quotation/{quotationId}")
    @Operation(summary = "Tạo phiếu yêu cầu mua hàng từ báo giá (tự động chọn tất cả items)")
    public ApiResponse<PurchaseRequestResponseDto> createFromQuotation(@PathVariable Long quotationId) {
        PurchaseRequest pr = purchaseRequestService.createPurchaseRequestFromQuotation(quotationId);
        PurchaseRequestResponseDto dto = PurchaseRequestResponseDto.fromEntity(pr);
        return ApiResponse.created("Tạo yêu cầu mua hàng thành công", dto);
    }

    @PostMapping("/from-quotation")
    @Operation(summary = "Tạo phiếu yêu cầu mua hàng từ báo giá với các items được chọn")
    public ApiResponse<PurchaseRequestResponseDto> createFromQuotationWithItems(
            @RequestBody CreatePurchaseRequestFromQuotationDto dto,
            @CurrentUser Employee currentUser) {
        PurchaseRequest pr = purchaseRequestService.createFromQuotation(dto, currentUser);
        PurchaseRequestResponseDto responseDto = PurchaseRequestResponseDto.fromEntity(pr);
        return ApiResponse.created("Tạo yêu cầu mua hàng thành công", responseDto);
    }

    @GetMapping("/{id}/quotation-items")
    @Operation(summary = "Lấy danh sách quotation items liên quan đến purchase request")
    public ApiResponse<List<PriceQuotationItemResponseDto>> getQuotationItems(
            @PathVariable Long id) {
        List<PriceQuotationItemResponseDto> items = purchaseRequestService
                .getQuotationItemsByPurchaseRequest(id);
        return ApiResponse.success("Lấy danh sách quotation items thành công", items);
    }

    @PutMapping("/{id}/approve")
    @Operation(summary = "Duyệt phiếu yêu cầu mua hàng và tạo phiếu nhập kho")
    public ApiResponse<PurchaseRequestResponseDto> approve(@PathVariable Long id) {
        PurchaseRequest pr = purchaseRequestService.approvePurchaseRequest(id);
        PurchaseRequestResponseDto dto = PurchaseRequestResponseDto.fromEntity(pr);
        return ApiResponse.success("Duyệt yêu cầu mua hàng thành công", dto);
    }

    @PutMapping("/{id}/reject")
    @Operation(summary = "Từ chối phiếu yêu cầu mua hàng")
    public ApiResponse<PurchaseRequestResponseDto> reject(
            @PathVariable Long id,
            @RequestParam(required = false) String reason) {
        PurchaseRequest pr = purchaseRequestService.rejectPurchaseRequest(id, reason);
        PurchaseRequestResponseDto dto = PurchaseRequestResponseDto.fromEntity(pr);
        return ApiResponse.success("Từ chối yêu cầu mua hàng thành công", dto);
    }

    @GetMapping
    @Operation(summary = "Danh sách phiếu yêu cầu mua hàng", description = "Lấy danh sách PR có phân trang + filter")
    public ApiResponse<Page<PurchaseRequestResponseDto>> getPurchaseRequests(
            @Parameter(description = "Từ khóa (mã PR / mã báo giá / tên khách hàng)") @RequestParam(required = false) String keyword,
            @Parameter(description = "Trạng thái duyệt (Chờ duyệt / Đã duyệt / Từ chối hoặc enum)") @RequestParam(required = false) String status,
            @Parameter(description = "Ngày tạo từ (yyyy-MM-dd)") @RequestParam(required = false) String fromDate,
            @Parameter(description = "Ngày tạo đến (yyyy-MM-dd)") @RequestParam(required = false) String toDate,
            @Parameter(description = "Trang hiện tại", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Số bản ghi mỗi trang", example = "10") @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<PurchaseRequestResponseDto> result = purchaseRequestService
                .getPurchaseRequests(keyword, status, fromDate, toDate, pageable);
        return ApiResponse.success("Lấy danh sách yêu cầu mua hàng thành công", result);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Chi tiết phiếu yêu cầu mua hàng", description = "Chi tiết + danh sách items")
    public ApiResponse<PurchaseRequestDetailDto> getPurchaseRequestDetail(@PathVariable Long id) {
        PurchaseRequestDetailDto dto = purchaseRequestService.getPurchaseRequestDetail(id);
        return ApiResponse.success("Lấy chi tiết yêu cầu mua hàng thành công", dto);
    }

    @GetMapping("/suggested-items")
    @Operation(summary = "Danh sách linh kiện đề xuất mua hàng cho kho (mở modal tạo PR)")
    public ApiResponse<List<PurchaseSuggestionItemDto>> getSuggestedItems() {
        List<PurchaseSuggestionItemDto> items = purchaseRequestService.getSuggestedPurchaseItems();
        return ApiResponse.success("Danh sách linh kiện đề xuất mua hàng", items);
    }

    @PostMapping("/manual")
    @Operation(summary = "Tạo phiếu yêu cầu mua hàng thủ công từ danh sách linh kiện")
    public ApiResponse<PurchaseRequestResponseDto> createManual(@RequestBody PurchaseRequestCreateDto requestDto) {
        PurchaseRequest pr = purchaseRequestService.createRequest(requestDto);
        PurchaseRequestResponseDto dto = PurchaseRequestResponseDto.fromEntity(pr);
        return ApiResponse.created("Tạo yêu cầu mua hàng thành công", dto);
    }
}
