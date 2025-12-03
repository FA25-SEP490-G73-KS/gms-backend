package fpt.edu.vn.gms.controller;

import fpt.edu.vn.gms.dto.request.ManualTransactionRequest;
import fpt.edu.vn.gms.dto.response.ApiResponse;
import fpt.edu.vn.gms.dto.response.ManualTransactionResponse;
import fpt.edu.vn.gms.service.WarehouseManualTransactionService;
import fpt.edu.vn.gms.utils.AppRoutes;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(AppRoutes.API_PREFIX + "/warehouse/manual-transaction")
@RequiredArgsConstructor
@Tag(name = "warehouse-manual-transaction-controller", description = "Tạo phiếu xuất/nhập kho thủ công")
public class WarehouseManualTransactionController {

    private final WarehouseManualTransactionService warehouseManualTransactionService;

    @PostMapping
    @Operation(summary = "Tạo phiếu xuất kho / nhập kho thủ công")
    public ApiResponse<ManualTransactionResponse> createManualTransaction(
            @RequestBody ManualTransactionRequest request
    ) {
        ManualTransactionResponse response = warehouseManualTransactionService.createManualTransaction(request);
        return ApiResponse.success("Tạo phiếu kho thủ công thành công", response);
    }
}

