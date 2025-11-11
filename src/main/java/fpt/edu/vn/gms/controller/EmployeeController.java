package fpt.edu.vn.gms.controller;

import fpt.edu.vn.gms.dto.EmployeeDto;
import fpt.edu.vn.gms.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@CrossOrigin(origins = "${fe-local-host}")
@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class EmployeeController {

    private final EmployeeService employeeService;

    @GetMapping("/technicians")
    public ResponseEntity<List<EmployeeDto>> getTechnicianDropdown() {
        return ResponseEntity.status(200).body(employeeService.findAllEmployeeIsTechniciansActive());
    }
}
