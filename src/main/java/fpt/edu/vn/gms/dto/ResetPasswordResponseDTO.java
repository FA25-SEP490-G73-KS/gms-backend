package fpt.edu.vn.gms.dto;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResetPasswordResponseDTO {

    private Long accountId;
    private String phone;
    private String roleName;
    private String message;
}
