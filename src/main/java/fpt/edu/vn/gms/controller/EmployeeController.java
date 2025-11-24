package fpt.edu.vn.gms.controller;

import fpt.edu.vn.gms.dto.EmployeeDto;
import fpt.edu.vn.gms.dto.response.ApiResponse;
import fpt.edu.vn.gms.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
    public ResponseEntity<ApiResponse<Page<EmployeeDto>>> getAllEmployees(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size
    ) {
        return ResponseEntity.ok(
                ApiResponse.success("Lấy danh sách nhân viên thành công",
                        employeeService.findAll(page, size))
        );
    }

    
}
