package fpt.edu.vn.gms.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import fpt.edu.vn.gms.entity.ServiceType;
import fpt.edu.vn.gms.service.ServiceTypeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ServiceTypeController.class)
class ServiceTypeControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    ServiceTypeService serviceTypeService;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void getAll_ShouldReturnListOfServiceTypes() throws Exception {
        ServiceType type1 = ServiceType.builder()
                .id(1L)
                .name("Bảo dưỡng")
                .build();
        ServiceType type2 = ServiceType.builder()
                .id(2L)
                .name("Sửa chữa")
                .build();
        when(serviceTypeService.getAll()).thenReturn(List.of(type1, type2));

        mockMvc.perform(get("/api/services"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.result").isArray())
                .andExpect(jsonPath("$.result.length()").value(2))
                .andExpect(jsonPath("$.result[0].id").value(1L))
                .andExpect(jsonPath("$.result[0].name").value("Bảo dưỡng"));
    }
}

