package fpt.edu.vn.gms.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class EmployeeInfoDto {
    private String fullName;
    private String phone;
    private String address;
    private String role;
}
