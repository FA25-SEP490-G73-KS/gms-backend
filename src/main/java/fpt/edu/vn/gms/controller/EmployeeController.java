package fpt.edu.vn.gms.controller;

import fpt.edu.vn.gms.dto.EmployeeDto;
import fpt.edu.vn.gms.dto.request.EmployeeCreateRequest;
import fpt.edu.vn.gms.dto.request.EmployeeUpdateRequest;
import fpt.edu.vn.gms.dto.response.ApiResponse;
import fpt.edu.vn.gms.dto.response.EmployeeDetailResponse;
import fpt.edu.vn.gms.dto.response.EmployeeListResponse;
import fpt.edu.vn.gms.dto.response.EmployeeResponse;
import fpt.edu.vn.gms.service.EmployeeService;
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

import java.util.List;

import static fpt.edu.vn.gms.utils.AppRoutes.EMPLOYEES_PREFIX;

@Tag(name = "employees", description = "Quản lý thông tin nhân viên")
@CrossOrigin(origins = "${fe-local-host}")
@RestController
@RequestMapping(path = EMPLOYEES_PREFIX, produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @GetMapping("/technicians")
    @Operation(summary = "Lấy danh sách kỹ thuật viên", description = "Lấy danh sách các kỹ thuật viên đang hoạt động để hiển thị trong dropdown.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lấy danh sách kỹ thuật viên thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Lỗi máy chủ nội bộ", content = @Content(schema = @Schema(hidden = true)))
    })
    public ResponseEntity<List<EmployeeDto>> getTechnicianDropdown() {
        return ResponseEntity.status(200).body(employeeService.findAllEmployeeIsTechniciansActive());
    }

    @GetMapping()
    @Operation(summary = "Lấy danh sách nhân viên")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lấy danh sách nhân viên thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Lỗi máy chủ nội bộ", content = @Content(schema = @Schema(hidden = true)))
    })
    public ResponseEntity<ApiResponse<Page<EmployeeListResponse>>> getAllEmployees(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size,
            @RequestParam(required = false) String status) {
        Page<EmployeeListResponse> result = employeeService.findAll(page, size, status);
        return ResponseEntity.ok(
                ApiResponse.success("Lấy danh sách nhân viên thành công", result));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Chi tiết nhân viên")
    public ResponseEntity<ApiResponse<EmployeeDetailResponse>> getEmployeeDetail(@PathVariable Long id) {
        EmployeeDetailResponse detail = employeeService.getEmployeeDetail(id);
        return ResponseEntity.ok(ApiResponse.success("Lấy chi tiết nhân viên thành công", detail));
    }

    @PatchMapping(value = "/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Cập nhật thông tin nhân viên")
    public ResponseEntity<ApiResponse<EmployeeDetailResponse>> updateEmployee(
            @PathVariable Long id,
            @Valid @RequestBody EmployeeUpdateRequest request) {
        EmployeeDetailResponse detail = employeeService.updateEmployee(id, request);
        return ResponseEntity.ok(ApiResponse.success("Cập nhật nhân viên thành công", detail));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "Tạo mới nhân viên")
    public ResponseEntity<ApiResponse<EmployeeResponse>> createEmployee(
            @Valid @RequestBody EmployeeCreateRequest request) {
        EmployeeResponse response = employeeService.createEmployee(request);
        return ResponseEntity.ok(ApiResponse.success("Tạo nhân viên thành công", response));
    }

    @PatchMapping("/{id}/active")
    @Operation(summary = "Cập nhật trạng thái hoạt động của nhân viên")
    public ResponseEntity<ApiResponse<Void>> updateEmployeeActiveStatus(
            @PathVariable Long id,
            @RequestParam("isActive") boolean isActive) {
        employeeService.updateEmployeeActiveStatus(id, isActive);
        return ResponseEntity.ok(ApiResponse.success("Cập nhật trạng thái hoạt động nhân viên thành công", null));
    }
}
