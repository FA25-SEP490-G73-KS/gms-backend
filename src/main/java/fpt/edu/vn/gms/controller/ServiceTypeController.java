package fpt.edu.vn.gms.controller;

import fpt.edu.vn.gms.common.annotations.Public;
import fpt.edu.vn.gms.dto.response.ApiResponse;
import fpt.edu.vn.gms.entity.ServiceType;
import fpt.edu.vn.gms.service.ServiceTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static fpt.edu.vn.gms.utils.AppRoutes.SERVICE_TYPES_PREFIX;

@Tag(name = "service_type", description = "Loại dịch vụ")
@CrossOrigin(origins = "${fe-local-host}")
@RestController
@RequestMapping(path = SERVICE_TYPES_PREFIX, produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class ServiceTypeController {

    ServiceTypeService serviceTypeService;

    @Public
    @GetMapping
    @Operation(
            summary = "Lấy tất cả loại dịch vụ",
            description = "API trả về danh sách tất cả Service Type trong hệ thống."
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Lấy danh sách loại dịch vụ thành công"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "500",
                    description = "Lỗi hệ thống"
            )
    })
    public ResponseEntity<ApiResponse<List<ServiceType>>> getAll() {
        return ResponseEntity.ok(
                ApiResponse.success("Lấy phiếu dịch vụ thành công", serviceTypeService.getAll())
        );
    }
}
