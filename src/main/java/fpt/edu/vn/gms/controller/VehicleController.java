package fpt.edu.vn.gms.controller;

import fpt.edu.vn.gms.dto.BrandDto;
import fpt.edu.vn.gms.dto.VehicleInfoDto;
import fpt.edu.vn.gms.dto.VehicleModelDto;
import fpt.edu.vn.gms.dto.response.ApiResponse;
import fpt.edu.vn.gms.dto.response.LicensePlateCheckResponseDto;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import jakarta.validation.Valid;
import fpt.edu.vn.gms.service.VehicleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

import static fpt.edu.vn.gms.utils.AppRoutes.VEHICLES_PREFIX;

@Tag(name = "vehicles", description = "Quản lý thông tin xe và hãng xe")
@CrossOrigin(origins = "${fe-local-host}")
@RestController
@RequestMapping(path = VEHICLES_PREFIX, produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class VehicleController {

    private final VehicleService vehicleService;

        @PostMapping("/check-license-plate")
        @Operation(
                summary = "Kiểm tra biển số xe đã tồn tại và customerId",
                description = "Kiểm tra biển số xe đã tồn tại trong hệ thống và customerId có trùng khớp không.",
                requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                        required = true,
                        content = @Content(
                                schema = @Schema(implementation = fpt.edu.vn.gms.dto.request.LicensePlateCheckRequest.class),
                                examples = @ExampleObject(
                                        name = "Example request",
                                        value = "{\n  \"licensePlate\": \"30A-12345\",\n  \"customerId\": 1\n}"
                                )
                        )
                ),
                responses = {
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(
                                responseCode = "200",
                                description = "Kết quả kiểm tra biển số xe",
                                content = @Content(
                                        schema = @Schema(implementation = fpt.edu.vn.gms.dto.response.LicensePlateCheckResponseDto.class),
                                        examples = @ExampleObject(
                                                name = "Example response",
                                                value = "{\n  \"isExists\": true,\n  \"isSameCustomer\": false,\n  \"licensePlate\": \"30A-12345\",\n  \"customerName\": \"Nguyen Van A\",\n  \"customerPhone\": \"0912345678\"\n}"
                                        )
                                )
                        )
                }
        )
        public ResponseEntity<LicensePlateCheckResponseDto> checkLicensePlateAndCustomer(
                        @Valid @RequestBody fpt.edu.vn.gms.dto.request.LicensePlateCheckRequest request) {
                LicensePlateCheckResponseDto response = vehicleService.checkLicensePlateAndCustomer(request.getLicensePlate(), request.getCustomerId());
                return ResponseEntity.ok(response);
        }


    @GetMapping("/brands")
    @Operation(summary = "Lấy tất cả các hãng xe", description = "Lấy danh sách tất cả các hãng xe có trong hệ thống.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lấy danh sách hãng xe thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Lỗi máy chủ nội bộ", content = @Content(schema = @Schema(hidden = true)))
    })
    public ResponseEntity<List<BrandDto>> getAllBrands() {
        return ResponseEntity.ok(vehicleService.getAllBrands());
    }

    @GetMapping("/brands/{brandId}/models")
    @Operation(summary = "Lấy các mẫu xe theo hãng", description = "Lấy danh sách các mẫu xe của một hãng xe cụ thể.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lấy danh sách mẫu xe thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy hãng xe", content = @Content(schema = @Schema(hidden = true))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Lỗi máy chủ nội bộ", content = @Content(schema = @Schema(hidden = true)))
    })
    public ResponseEntity<List<VehicleModelDto>> getModelsByBrand(@PathVariable Long brandId) {
        return ResponseEntity.ok(vehicleService.getModelsByBrand(brandId));
    }

    @GetMapping
    @Operation(summary = "Lấy thông tin xe theo biển số", description = "Lấy thông tin chi tiết của một chiếc xe bằng biển số.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lấy thông tin xe thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy xe", content = @Content(schema = @Schema(hidden = true))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Lỗi máy chủ nội bộ", content = @Content(schema = @Schema(hidden = true)))
    })
    public ResponseEntity<ApiResponse<VehicleInfoDto>> getLicensePlate(
            @RequestParam String licensePlate) {

        VehicleInfoDto dto = vehicleService.findByLicensePlate(licensePlate);

        return ResponseEntity.status(200)
                .body(ApiResponse.success("Thành công!!!", dto));
    }

}
