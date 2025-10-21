package fpt.edu.vn.gms.controller;

import fpt.edu.vn.gms.dto.EmployeeDto;
import fpt.edu.vn.gms.entity.Employee;
import fpt.edu.vn.gms.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeController {
    private final EmployeeService employeeService;

    /**
     * Lây danh sách nhân viên với phân trang
     * @param page sô trang
     * @param size kích thước trang
     * @return
     */
    @GetMapping
    public ResponseEntity<Page<EmployeeDto>> getAllEmployees( @RequestParam(defaultValue = "0") int page,
                                                              @RequestParam(defaultValue = "6") int size) {
        return ResponseEntity.ok(employeeService.getAllEmployee(page, size));
    }

    /**
     * Lấy nhân viên theo ID
     * @param employeeId
     * @return
     */
    @GetMapping("/{employeeId}")
    public ResponseEntity<EmployeeDto> getEmployeeByEmployeeId(@PathVariable Long employeeId) {
        return ResponseEntity.ok(employeeService.getEmployeeByEmployeeId(employeeId));
    }

    /**
     * Lấy danh sách kỹ thuật viên đang hoạt động
     * @return
     */
    @GetMapping("/technicians/active")
    public ResponseEntity<List<Employee>> getAllActiveTechnicians() {
        List<Employee> activeTechnicians = employeeService.findAllEmployeeIsTechniciansActive();
        return ResponseEntity.ok(activeTechnicians);
    }
}
