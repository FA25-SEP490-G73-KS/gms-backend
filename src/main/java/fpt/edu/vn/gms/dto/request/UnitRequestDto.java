package fpt.edu.vn.gms.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UnitRequestDto {

    @NotBlank(message = "Tên đơn vị không được để trống")
    private String name;
}

