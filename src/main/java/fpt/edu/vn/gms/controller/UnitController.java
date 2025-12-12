package fpt.edu.vn.gms.controller;

import fpt.edu.vn.gms.dto.request.UnitRequestDto;
import fpt.edu.vn.gms.dto.response.ApiResponse;
import fpt.edu.vn.gms.dto.response.UnitResponseDto;
import fpt.edu.vn.gms.service.UnitService;
import fpt.edu.vn.gms.utils.AppRoutes;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = AppRoutes.UNIT_PREFIX, produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Tag(name = "unit-controller", description = "Quản lý đơn vị linh kiện")
public class UnitController {

    private final UnitService unitService;

    @GetMapping
    @Operation(summary = "Lấy danh sách đơn vị linh kiện", description = "Trả về danh sách phân trang đơn vị linh kiện")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Thành công")
    public ResponseEntity<ApiResponse<Page<UnitResponseDto>>> getAll(@ParameterObject Pageable pageable) {
        return ResponseEntity.ok(
                ApiResponse.success("Danh sách đơn vị!", unitService.getAll(pageable))
        );
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lấy chi tiết đơn vị linh kiện", description = "Trả về thông tin chi tiết của một đơn vị linh kiện theo ID")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Thành công")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy",
            content = @Content(schema = @Schema(hidden = true)))
    public ResponseEntity<ApiResponse<UnitResponseDto>> getById(@PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.success("Xem chi tiết đơn vị", unitService.getById(id))
        );
    }

    @PostMapping
    @Operation(summary = "Tạo mới đơn vị linh kiện", description = "Tạo mới một đơn vị linh kiện")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Tạo thành công")
    public ResponseEntity<ApiResponse<UnitResponseDto>> create(@Valid @RequestBody UnitRequestDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Tạo mới đơn vị!", unitService.create(dto)));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Cập nhật đơn vị linh kiện", description = "Cập nhật thông tin đơn vị linh kiện theo ID")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Cập nhật thành công")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy",
            content = @Content(schema = @Schema(hidden = true)))
    public ResponseEntity<ApiResponse<UnitResponseDto>> update(@PathVariable Long id,
                                                               @Valid @RequestBody UnitRequestDto dto) {
        return ResponseEntity.ok(
                ApiResponse.success("Cập nhật đơn vị!", unitService.update(id, dto))
        );
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Xóa đơn vị linh kiện", description = "Xóa một đơn vị linh kiện theo ID")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "Xóa thành công")
    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy",
            content = @Content(schema = @Schema(hidden = true)))
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        unitService.delete(id);
    }
}
