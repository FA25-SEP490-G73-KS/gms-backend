package fpt.edu.vn.gms.controller;

import fpt.edu.vn.gms.dto.EmployeeDto;
import fpt.edu.vn.gms.entity.Employee;
import fpt.edu.vn.gms.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller cho các thao tác liên quan đến Nhân viên (Employee).
 * Cung cấp API để truy xuất danh sách, chi tiết nhân viên và lọc kỹ thuật viên đang hoạt động.
 */
@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
@Tag(name = "Employee Management", description = "Quản lý thông tin nhân viên trong hệ thống garage")
public class EmployeeController {

    private final EmployeeService employeeService;

    // ============================================================
    // 1️⃣ GET ALL EMPLOYEES (PAGINATED)
    // ============================================================

    @Operation(
            summary = "Lấy danh sách nhân viên (phân trang)",
            description = "Trả về danh sách nhân viên theo từng trang, mỗi nhân viên được ánh xạ sang EmployeeDto.",
            parameters = {
                    @Parameter(name = "page", description = "Số trang (bắt đầu từ 0)", example = "0"),
                    @Parameter(name = "size", description = "Số lượng nhân viên trên mỗi trang", example = "6")
            }
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lấy danh sách thành công",
                    content = @Content(schema = @Schema(implementation = EmployeeDto.class))),
            @ApiResponse(responseCode = "500", description = "Lỗi server nội bộ", content = @Content)
    })
    @GetMapping
    public ResponseEntity<Page<EmployeeDto>> getAllEmployees(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size) {

        Page<EmployeeDto> employees = employeeService.getAllEmployee(page, size);
        return ResponseEntity.ok(employees);
    }

    // ============================================================
    // 2️⃣ GET EMPLOYEE BY ID
    // ============================================================

    @Operation(
            summary = "Lấy thông tin chi tiết nhân viên theo ID",
            description = "Trả về thông tin chi tiết của một nhân viên theo mã định danh employeeId."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lấy thông tin nhân viên thành công",
                    content = @Content(schema = @Schema(implementation = EmployeeDto.class))),
            @ApiResponse(responseCode = "404", description = "Không tìm thấy nhân viên", content = @Content)
    })
    @GetMapping("/{employeeId}")
    public ResponseEntity<EmployeeDto> getEmployeeByEmployeeId(
            @Parameter(description = "ID của nhân viên cần tìm", example = "1")
            @PathVariable Long employeeId) {

        EmployeeDto employee = employeeService.getEmployeeByEmployeeId(employeeId);
        return ResponseEntity.ok(employee);
    }

    // ============================================================
    // 3️⃣ GET ACTIVE TECHNICIANS
    // ============================================================

    @Operation(
            summary = "Lấy danh sách kỹ thuật viên đang hoạt động",
            description = "Trả về danh sách tất cả kỹ thuật viên (Technician) có trạng thái ACTIVE."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lấy danh sách kỹ thuật viên thành công",
                    content = @Content(schema = @Schema(implementation = Employee.class))),
            @ApiResponse(responseCode = "204", description = "Không có kỹ thuật viên nào đang hoạt động", content = @Content)
    })
    @GetMapping("/technicians/active")
    public ResponseEntity<List<Employee>> getAllActiveTechnicians() {
        List<Employee> activeTechnicians = employeeService.findAllEmployeeIsTechniciansActive();
        if (activeTechnicians.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(activeTechnicians);
    }
}
