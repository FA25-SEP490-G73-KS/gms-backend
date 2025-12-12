package fpt.edu.vn.gms.controller;

import fpt.edu.vn.gms.common.annotations.CurrentUser;
import fpt.edu.vn.gms.dto.request.AllowanceRequestDto;
import fpt.edu.vn.gms.dto.response.AllowanceDto;
import fpt.edu.vn.gms.entity.Employee;
import fpt.edu.vn.gms.service.AllowanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/allowance")
@RequiredArgsConstructor
@Tag(name = "allowance-controller", description = "Quản lý phụ cấp nhân viên")
public class AllowanceController {

        private final AllowanceService allowanceService;

        @Operation(summary = "Tạo phụ cấp cho nhân viên", description = """
                        Tạo mới một phụ cấp cho nhân viên.
                        Các giá trị gồm:
                        - Loại phụ cấp (enum AllowanceType)
                        - Số tiền
                        - Người tạo (creatorId)
                        """)
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Tạo phụ cấp thành công")
        })
        @PostMapping("/create")
        public ResponseEntity<fpt.edu.vn.gms.dto.response.ApiResponse<AllowanceDto>> createAllowance(
                        @Valid @RequestBody AllowanceRequestDto dto,
                        @Parameter(description = "ID người tạo") @CurrentUser Employee creator) {

                return ResponseEntity.ok(
                                fpt.edu.vn.gms.dto.response.ApiResponse.success("Tạo phụ cấp thành công",
                                                allowanceService.createAllowance(dto, creator)));
        }

        @DeleteMapping("/{id}")
        @Operation(summary = "Xóa phụ cấp", description = "Chỉ xóa theo ID phụ cấp.")
        @ApiResponses({
                        @ApiResponse(responseCode = "200", description = "Xóa phụ cấp thành công"),
                        @ApiResponse(responseCode = "404", description = "Không tìm thấy phụ cấp")
        })
        public ResponseEntity<fpt.edu.vn.gms.dto.response.ApiResponse<Void>> deleteAllowance(
                        @PathVariable Long id) {
                allowanceService.deleteAllowance(id);
                return ResponseEntity.ok(
                                fpt.edu.vn.gms.dto.response.ApiResponse.success("Xóa phụ cấp thành công", null));
        }
}
