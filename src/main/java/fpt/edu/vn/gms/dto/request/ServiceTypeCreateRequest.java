package fpt.edu.vn.gms.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceTypeCreateRequest {

    @NotBlank(message = "Tên loại dịch vụ không được để trống")
    private String name;
}

