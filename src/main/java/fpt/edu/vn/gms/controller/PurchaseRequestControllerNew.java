package fpt.edu.vn.gms.controller;

import fpt.edu.vn.gms.dto.response.ApiResponse;
import fpt.edu.vn.gms.dto.response.PurchaseRequestDetailDto;
import fpt.edu.vn.gms.dto.response.PurchaseRequestResponseDto;
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

@RestController
@RequestMapping("/api/purchase-requests")
@RequiredArgsConstructor
@Tag(name = "purchase-request-controller", description = "Tạo và duyệt phiếu yêu cầu mua hàng")
public class PurchaseRequestControllerNew {

    private final PurchaseRequestService purchaseRequestService;

    @PostMapping("/from-quotation/{quotationId}")
    @Operation(summary = "Tạo phiếu yêu cầu mua hàng từ báo giá")
    public ApiResponse<PurchaseRequestResponseDto> createFromQuotation(@PathVariable Long quotationId) {
        PurchaseRequest pr = purchaseRequestService.createPurchaseRequestFromQuotation(quotationId);
        PurchaseRequestResponseDto dto = PurchaseRequestResponseDto.fromEntity(pr);
        return ApiResponse.created("Tạo yêu cầu mua hàng thành công", dto);
    }

    @PutMapping("/{id}/approve")
    @Operation(summary = "Duyệt phiếu yêu cầu mua hàng và tạo phiếu nhập kho")
    public ApiResponse<PurchaseRequestResponseDto> approve(@PathVariable Long id) {
        PurchaseRequest pr = purchaseRequestService.approvePurchaseRequest(id);
        PurchaseRequestResponseDto dto = PurchaseRequestResponseDto.fromEntity(pr);
        return ApiResponse.success("Duyệt yêu cầu mua hàng thành công", dto);
    }

    @GetMapping
    @Operation(summary = "Danh sách phiếu yêu cầu mua hàng", description = "Lấy danh sách PR có phân trang + filter")
    public ApiResponse<Page<PurchaseRequestResponseDto>> getPurchaseRequests(
            @Parameter(description = "Từ khóa (mã PR / mã báo giá / tên khách hàng)")
            @RequestParam(required = false) String keyword,
            @Parameter(description = "Trạng thái duyệt (Chờ duyệt / Đã duyệt / Từ chối hoặc enum)")
            @RequestParam(required = false) String status,
            @Parameter(description = "Ngày tạo từ (yyyy-MM-dd)")
            @RequestParam(required = false) String fromDate,
            @Parameter(description = "Ngày tạo đến (yyyy-MM-dd)")
            @RequestParam(required = false) String toDate,
            @Parameter(description = "Trang hiện tại", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Số bản ghi mỗi trang", example = "10")
            @RequestParam(defaultValue = "10") int size
    ) {
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
}
