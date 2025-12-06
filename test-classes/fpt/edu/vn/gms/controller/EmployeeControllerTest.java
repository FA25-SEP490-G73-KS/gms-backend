package fpt.edu.vn.gms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import fpt.edu.vn.gms.dto.EmployeeDto;
import fpt.edu.vn.gms.dto.response.EmployeeListResponse;
import fpt.edu.vn.gms.service.EmployeeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EmployeeController.class)
class EmployeeControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    EmployeeService employeeService;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void getTechnicianDropdown_ShouldReturnListOfTechnicians() throws Exception {
        EmployeeDto tech1 = new EmployeeDto(1L, "Tech 1", "0912345678");
        EmployeeDto tech2 = new EmployeeDto(2L, "Tech 2", "0987654321");
        when(employeeService.findAllEmployeeIsTechniciansActive())
                .thenReturn(List.of(tech1, tech2));

        mockMvc.perform(get("/api/employees/technicians"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Tech 1"));
    }

    @Test
    void getAllEmployees_ShouldReturnPagedDtos() throws Exception {
        EmployeeListResponse emp1 = EmployeeListResponse.builder()
                .employeeId(1L)
                .fullName("Employee 1")
                .phone("0912345678")
                .build();
        Page<EmployeeListResponse> page = new PageImpl<>(List.of(emp1), PageRequest.of(0, 6), 1);
        when(employeeService.findAll(0, 6, null)).thenReturn(page);

        mockMvc.perform(get("/api/employees")
                        .param("page", "0")
                        .param("size", "6"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.content").isArray())
                .andExpect(jsonPath("$.result.content[0].employeeId").value(1L))
                .andExpect(jsonPath("$.result.content[0].fullName").value("Employee 1"));
    }
}

