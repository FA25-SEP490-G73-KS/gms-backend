package fpt.edu.vn.gms.controller;

import fpt.edu.vn.gms.dto.request.PartUpdateReqDto;
import fpt.edu.vn.gms.dto.response.PartReqDto;
import fpt.edu.vn.gms.dto.response.ApiResponse;
import fpt.edu.vn.gms.mapper.PartMapper;
import fpt.edu.vn.gms.service.PartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static fpt.edu.vn.gms.utils.AppRoutes.PARTS_PREFIX;

@Tag(name = "parts", description = "Quản lý thông tin linh kiện và phụ tùng")
@CrossOrigin(origins = "${fe-local-host}")
@RestController
@RequestMapping(path = PARTS_PREFIX, produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class PartController {

        private final PartService partService;
        private final PartMapper partMapper;

        @GetMapping
        @Operation(summary = "Tìm tất cả linh kiện", description = "Lấy danh sách tất cả các linh kiện với phân trang.")
        @ApiResponses(value = {
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lấy danh sách linh kiện thành công"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Lỗi máy chủ nội bộ", content = @Content(schema = @Schema(hidden = true)))
        })
        public ResponseEntity<ApiResponse<Page<PartReqDto>>> findAll(
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "6") int size) {

                Page<PartReqDto> partList = partService.getAllPart(page, size);

                return ResponseEntity.status(200)
                                .body(ApiResponse.success("Danh sách linh kiện", partList));
        }

        @GetMapping("/{id}")
        @Operation(
                summary = "Lấy linh kiện theo ID",
                description = "API trả về thông tin chi tiết một linh kiện theo ID."
        )
        @ApiResponses(value = {
                @io.swagger.v3.oas.annotations.responses.ApiResponse(
                        responseCode = "200",
                        description = "Lấy linh kiện thành công",
                        content = @Content(mediaType = "application/json",
                                schema = @Schema(implementation = PartReqDto.class))
                ),
                @io.swagger.v3.oas.annotations.responses.ApiResponse(
                        responseCode = "404",
                        description = "Không tìm thấy linh kiện",
                        content = @Content(schema = @Schema(hidden = true))
                ),
                @io.swagger.v3.oas.annotations.responses.ApiResponse(
                        responseCode = "500",
                        description = "Lỗi máy chủ nội bộ",
                        content = @Content(schema = @Schema(hidden = true))
                )
        })
        public ResponseEntity<ApiResponse<PartReqDto>> getPartById(@PathVariable Long id) {

                PartReqDto dto = partService.getPartById(id);

                return ResponseEntity.ok(
                        ApiResponse.success("Thông tin linh kiện", dto)
                );
        }


        @PostMapping
        @Operation(summary = "Tạo linh kiện mới", description = "Tạo một linh kiện mới dựa trên thông tin được cung cấp.")
        @ApiResponses(value = {
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Tạo linh kiện thành công"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Yêu cầu không hợp lệ", content = @Content(schema = @Schema(hidden = true))),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Lỗi máy chủ nội bộ", content = @Content(schema = @Schema(hidden = true)))
        })
        public ResponseEntity<ApiResponse<PartReqDto>> createPart(
                        @RequestBody PartUpdateReqDto part) {

                PartReqDto resDto = partService.createPart(part);
                return ResponseEntity.status(201)
                                .body(ApiResponse.success("Thêm linh kiện!!!!", resDto));
        }

        @PatchMapping("/{id}")
        @Operation(summary = "Cập nhật linh kiện", description = "Cập nhật thông tin chi tiết một linh kiện.")
        @ApiResponses(value = {
                @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Cập nhật thành công"),
                @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy linh kiện"),
                @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Yêu cầu không hợp lệ")
        })
        public ResponseEntity<ApiResponse<PartReqDto>> updatePart(
                @PathVariable Long id,
                @Valid @RequestBody PartUpdateReqDto dto) {

                PartReqDto updated = partService.updatePart(id, dto);
                return ResponseEntity.ok(ApiResponse.success("Cập nhật linh kiện thành công", updated));
        }

        @GetMapping("/category")
        @Operation(summary = "Lấy linh kiện theo danh mục", description = "Lấy danh sách các linh kiện theo tên danh mục với phân trang.")
        @ApiResponses(value = {
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lấy danh sách linh kiện thành công"),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Tên danh mục không hợp lệ", content = @Content(schema = @Schema(hidden = true))),
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Lỗi máy chủ nội bộ", content = @Content(schema = @Schema(hidden = true)))
        })
        public ResponseEntity<ApiResponse<Page<PartReqDto>>> getPartByCategory(
                        @RequestParam String categoryName,
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "6") int size) {

                Page<PartReqDto> resDtoPage = partService.getPartByCategory(categoryName, page, size);

                return ResponseEntity.status(200)
                                .body(ApiResponse.success("Part có category " + categoryName, resDtoPage));
        }


}
