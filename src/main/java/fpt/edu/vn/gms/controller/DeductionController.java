package fpt.edu.vn.gms.controller;

import fpt.edu.vn.gms.common.annotations.CurrentUser;
import fpt.edu.vn.gms.dto.request.DeductionRequestDto;
import fpt.edu.vn.gms.dto.response.DeductionDto;
import fpt.edu.vn.gms.entity.Employee;
import fpt.edu.vn.gms.service.DeductionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/deduction")
@RequiredArgsConstructor
@Tag(name = "deduction-controller", description = "Quản lý khấu trừ lương nhân viên")
public class DeductionController {

    private final DeductionService deductionService;

    @Operation(
            summary = "Tạo khấu trừ",
            description = """
                Tạo mới một khoản khấu trừ cho nhân viên.
                Bao gồm:
                - Danh mục (DeductionType)
                - Nội dung mô tả
                - Số tiền
                """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Tạo khấu trừ thành công")
    })
    @PostMapping()
    public ResponseEntity<fpt.edu.vn.gms.dto.response.ApiResponse<DeductionDto>> createDeduction(
            @Valid @RequestBody DeductionRequestDto dto,
            @Parameter(description = "ID người tạo") @CurrentUser Employee employee) {

        return ResponseEntity.ok(
                fpt.edu.vn.gms.dto.response.ApiResponse.success("Tạo khấu trừ thành công",
                        deductionService.createDeduction(dto, employee))
        );
    }
}

