package fpt.edu.vn.gms.controller;

import fpt.edu.vn.gms.entity.Brand;
import fpt.edu.vn.gms.service.BrandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/brands")
@RequiredArgsConstructor
@Tag(name = "brand-controller", description = "Quản lý thông tin thương hiệu xe")
public class BrandController {

    private final BrandService brandService;

    @GetMapping
    @Operation(
            summary = "Lấy danh sách tất cả brand",
            description = "Trả về toàn bộ thương hiệu xe trong hệ thống.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Danh sách thương hiệu",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Brand.class)
                            )
                    )
            }
    )
    public ResponseEntity<fpt.edu.vn.gms.dto.response.ApiResponse<List<Brand>>> getAllBrands() {
        List<Brand> brands = brandService.getAll();
        return ResponseEntity.ok(fpt.edu.vn.gms.dto.response.ApiResponse.success("Danh sách hãng xe!", brands));
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Lấy thương hiệu theo ID",
            description = "Trả về thông tin thương hiệu tương ứng với ID.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Thông tin thương hiệu",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = Brand.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Không tìm thấy thương hiệu",
                            content = @Content
                    )
            }
    )
    public ResponseEntity<fpt.edu.vn.gms.dto.response.ApiResponse<Brand>> getBrandById(
            @PathVariable Long id
    ) {
        Brand brand = brandService.getById(id);
        if (brand == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(fpt.edu.vn.gms.dto.response.ApiResponse.success("Hãng xe!", brand));
    }
}
