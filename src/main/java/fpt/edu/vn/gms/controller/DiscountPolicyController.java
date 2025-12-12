package fpt.edu.vn.gms.controller;

import fpt.edu.vn.gms.dto.request.DiscountPolicyRequestDto;
import fpt.edu.vn.gms.dto.response.ApiResponse;
import fpt.edu.vn.gms.dto.response.DiscountPolicyResponseDto;
import fpt.edu.vn.gms.service.DiscountPolicyService;
import fpt.edu.vn.gms.utils.AppRoutes;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = AppRoutes.DISCOUNT_POLICY_PREFIX, produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Tag(name = "discount-policies", description = "Quản lý chính sách giảm giá cho khách hàng")
public class DiscountPolicyController {
  private final DiscountPolicyService discountPolicyService;

  @GetMapping
  @Operation(summary = "Lấy danh sách chính sách giảm giá", description = "Trả về danh sách phân trang các chính sách giảm giá")
  @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Thành công")
  public ResponseEntity<ApiResponse<Page<DiscountPolicyResponseDto>>> getAll(@ParameterObject Pageable pageable) {
    return ResponseEntity.ok(ApiResponse.success("", discountPolicyService.getAll(pageable)));
  }

  @GetMapping("/{id}")
  @Operation(summary = "Lấy chi tiết chính sách giảm giá", description = "Trả về thông tin chi tiết của một chính sách giảm giá theo ID")
  @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Thành công")
  @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy", content = @Content(schema = @Schema(hidden = true)))
  public ResponseEntity<ApiResponse<DiscountPolicyResponseDto>> getById(@PathVariable Long id) {
    return ResponseEntity.ok(ApiResponse.success("", discountPolicyService.getById(id)));
  }

  @PostMapping
  @Operation(summary = "Tạo mới chính sách giảm giá", description = "Tạo mới một chính sách giảm giá cho khách hàng")
  @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Tạo thành công")
  public ResponseEntity<ApiResponse<DiscountPolicyResponseDto>> create(@RequestBody DiscountPolicyRequestDto dto) {
    return ResponseEntity.status(201).body(ApiResponse.success("", discountPolicyService.create(dto)));
  }

  @PatchMapping("/{id}")
  @Operation(summary = "Cập nhật chính sách giảm giá", description = "Cập nhật thông tin một chính sách giảm giá theo ID")
  @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Cập nhật thành công")
  @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy", content = @Content(schema = @Schema(hidden = true)))
  public ResponseEntity<ApiResponse<DiscountPolicyResponseDto>> update(@PathVariable Long id,
      @RequestBody DiscountPolicyRequestDto dto) {
    return ResponseEntity.ok(ApiResponse.success("", discountPolicyService.update(id, dto)));
  }

  @DeleteMapping("/{id}")
  @Operation(summary = "Xóa chính sách giảm giá", description = "Xóa một chính sách giảm giá theo ID")
  @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Xóa thành công")
  @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy", content = @Content(schema = @Schema(hidden = true)))
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public void delete(@PathVariable Long id) {
    discountPolicyService.delete(id);
  }
}
