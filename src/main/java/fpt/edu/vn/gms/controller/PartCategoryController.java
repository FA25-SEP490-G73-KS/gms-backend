package fpt.edu.vn.gms.controller;

import fpt.edu.vn.gms.entity.PartCategory;
import fpt.edu.vn.gms.service.PartCategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static fpt.edu.vn.gms.utils.AppRoutes.PART_CATEGORY_PREFIX;

@Tag(name = "part_category", description = "Loại linh kiện")
@CrossOrigin(origins = "${fe-local-host}")
@RestController
@RequestMapping(path = PART_CATEGORY_PREFIX, produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class PartCategoryController {

    PartCategoryService partCategoryService;

    @GetMapping
    @Operation(summary = "Lấy danh sách tất cả loại linh kiện")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lấy danh sách thành công"),
            @ApiResponse(responseCode = "500", description = "Lỗi hệ thống")
    })
    public ResponseEntity<fpt.edu.vn.gms.dto.response.ApiResponse<List<PartCategory>>> getAll() {
        return ResponseEntity.ok(
                fpt.edu.vn.gms.dto.response.ApiResponse.success("Lấy danh sách loại linh kiện thành công", partCategoryService.getAll())
        );
    }

}
