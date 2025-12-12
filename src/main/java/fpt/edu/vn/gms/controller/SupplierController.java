package fpt.edu.vn.gms.controller;

import fpt.edu.vn.gms.dto.request.SupplierRequestDto;
import fpt.edu.vn.gms.dto.response.SupplierResponseDto;
import fpt.edu.vn.gms.service.SupplierService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@CrossOrigin(origins = "${fe-local-host}")
@RestController
@RequestMapping(path = "/api/suppliers", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class SupplierController {
    private final SupplierService supplierService;

    @GetMapping
    @Operation(summary = "Lấy danh sách nhà cung cấp", description = "Lấy danh sách tất cả nhà cung cấp với phân trang.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lấy danh sách nhà cung cấp thành công", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SupplierResponseDto.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Lỗi máy chủ nội bộ", content = @Content(schema = @Schema(hidden = true)))
    })
    public ResponseEntity<Page<SupplierResponseDto>> getAllSuppliers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size) {
        return ResponseEntity.ok(supplierService.getAllSuppliers(page, size));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lấy nhà cung cấp theo ID", description = "Lấy thông tin chi tiết của một nhà cung cấp dựa trên ID.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lấy thông tin nhà cung cấp thành công", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SupplierResponseDto.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy nhà cung cấp", content = @Content(schema = @Schema(hidden = true))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Lỗi máy chủ nội bộ", content = @Content(schema = @Schema(hidden = true)))
    })
    public ResponseEntity<SupplierResponseDto> getSupplierById(@PathVariable Long id) {
        return ResponseEntity.ok(supplierService.getSupplierById(id));
    }

    @PostMapping
    @Operation(summary = "Tạo mới nhà cung cấp", description = "Tạo một nhà cung cấp mới với thông tin được cung cấp.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Tạo nhà cung cấp thành công", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SupplierResponseDto.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Yêu cầu không hợp lệ", content = @Content(schema = @Schema(hidden = true))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Lỗi máy chủ nội bộ", content = @Content(schema = @Schema(hidden = true)))
    })
    public ResponseEntity<SupplierResponseDto> createSupplier(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Thông tin nhà cung cấp", required = true, content = @Content(schema = @Schema(implementation = SupplierRequestDto.class))) @Valid @org.springframework.web.bind.annotation.RequestBody SupplierRequestDto supplierRequestDto) {
        return ResponseEntity.ok(supplierService.createSupplier(supplierRequestDto));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Cập nhật nhà cung cấp", description = "Cập nhật thông tin của một nhà cung cấp đã có.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Cập nhật nhà cung cấp thành công", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SupplierResponseDto.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Yêu cầu không hợp lệ", content = @Content(schema = @Schema(hidden = true))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy nhà cung cấp", content = @Content(schema = @Schema(hidden = true))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Lỗi máy chủ nội bộ", content = @Content(schema = @Schema(hidden = true)))
    })
    public ResponseEntity<SupplierResponseDto> updateSupplier(
            @PathVariable Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Thông tin nhà cung cấp cập nhật", required = true, content = @Content(schema = @Schema(implementation = SupplierRequestDto.class))) @Valid @org.springframework.web.bind.annotation.RequestBody SupplierRequestDto supplierRequestDto) {
        return ResponseEntity.ok(supplierService.updateSupplier(id, supplierRequestDto));
    }

    @PatchMapping("/{id}/toggle-active")
    @Operation(summary = "Toggle trạng thái nhà cung cấp", description = "Chuyển đổi trạng thái isActive của nhà cung cấp (true -> false, false -> true).")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Cập nhật trạng thái nhà cung cấp thành công", content = @Content(mediaType = "application/json", schema = @Schema(implementation = SupplierResponseDto.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy nhà cung cấp", content = @Content(schema = @Schema(hidden = true))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Lỗi máy chủ nội bộ", content = @Content(schema = @Schema(hidden = true)))
    })
    public ResponseEntity<SupplierResponseDto> toggleSupplierActiveStatus(
            @io.swagger.v3.oas.annotations.Parameter(description = "ID nhà cung cấp", required = true) @PathVariable Long id) {
        return ResponseEntity.ok(supplierService.toggleSupplierActiveStatus(id));
    }
}
