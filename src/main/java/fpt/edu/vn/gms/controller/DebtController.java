package fpt.edu.vn.gms.controller;

import fpt.edu.vn.gms.common.enums.DebtStatus;
import fpt.edu.vn.gms.dto.response.ApiResponse;
import fpt.edu.vn.gms.service.DebtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import static fpt.edu.vn.gms.utils.AppRoutes.DEBTS_PREFIX;

@Tag(name = "debts", description = "Quản lý công nợ")
@CrossOrigin(origins = "${fe-local-host}")
@RestController
@RequestMapping(path = DEBTS_PREFIX, produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DebtController {

    DebtService debtService;

    @GetMapping
    @Operation(summary = "Lấy danh sách công nợ theo khách hàng")
    public ResponseEntity<ApiResponse<?>> getDebts(
            @RequestParam Long customerId,
            @RequestParam(required = false) DebtStatus status,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort
    ) {
        var result = debtService.getDebtsByCustomer(customerId, status, keyword, page, size, sort);
        return ResponseEntity.ok(ApiResponse.success("Lấy danh sách công nợ thành công", result));
    }
}