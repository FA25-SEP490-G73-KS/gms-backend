package fpt.edu.vn.gms.controller;

import fpt.edu.vn.gms.common.enums.DebtStatus;
import fpt.edu.vn.gms.dto.CreateDebtDto;
import fpt.edu.vn.gms.dto.response.ApiResponse;
import fpt.edu.vn.gms.dto.response.DebtResDto;
import fpt.edu.vn.gms.service.DebtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
    public ResponseEntity<ApiResponse<Page<DebtResDto>>> getDebts(
            @RequestParam Long customerId,
            @RequestParam(required = false) DebtStatus status,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort) {
        var result = debtService.getDebtsByCustomer(customerId, status, keyword, page, size, sort);
        return ResponseEntity.ok(ApiResponse.success("Lấy danh sách công nợ thành công", result));
    }

    @Operation(summary = "Tạo công nợ", description = "Tạo mới một khoản công nợ cho khách hàng")
    @io.swagger.v3.oas.annotations.responses.ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Tạo công nợ thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Yêu cầu không hợp lệ", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(hidden = true))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy khách hàng", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(hidden = true))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Lỗi máy chủ nội bộ", content = @io.swagger.v3.oas.annotations.media.Content(schema = @io.swagger.v3.oas.annotations.media.Schema(hidden = true)))
    })
    @PostMapping
    public ResponseEntity<ApiResponse<DebtResDto>> createDebt(
            @RequestBody @Valid CreateDebtDto createDebtDto) {
        return ResponseEntity.ok(ApiResponse.success("Tạo công nợ thành công", debtService.createDebt(createDebtDto)));
    }
}