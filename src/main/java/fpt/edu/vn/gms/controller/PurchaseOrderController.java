package fpt.edu.vn.gms.controller;

import fpt.edu.vn.gms.dto.response.ApiResponse;
import fpt.edu.vn.gms.dto.response.PurchaseOrderResponseDto;
import fpt.edu.vn.gms.service.PurchaseOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "${fe-local-host}")
@RestController
@RequestMapping("/api/purchase-orders")
@RequiredArgsConstructor
public class PurchaseOrderController {

    private final PurchaseOrderService purchaseOrderService;

    @PostMapping("/from-request/{requestId}")
    public ResponseEntity<ApiResponse<PurchaseOrderResponseDto>> createPurchaseOrderFromRequest(
            @PathVariable Long requestId
    ) {

        PurchaseOrderResponseDto response = purchaseOrderService.createFromPurchaseRequest(requestId);

        return ResponseEntity.status(201)
                .body(ApiResponse.success("Phiếu mua hàng " + response.getCode() + " đã được tạo!!!", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<PurchaseOrderResponseDto>>> getAllPurchaseOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size
    ) {

        Page<PurchaseOrderResponseDto> responseDtos = purchaseOrderService.getAll(page, size);

        return ResponseEntity.status(200)
                .body(ApiResponse.success("Thành công!!", responseDtos));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PurchaseOrderResponseDto>> getPurchaseOrderById(
            @PathVariable Long id
    ) {

        PurchaseOrderResponseDto response = purchaseOrderService.getById(id);

        return ResponseEntity.status(200)
                .body(ApiResponse.success("Chi tiết phiếu mua hàng!!!", response));
    }

}


