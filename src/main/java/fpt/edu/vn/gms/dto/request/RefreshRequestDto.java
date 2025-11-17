package fpt.edu.vn.gms.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RefreshRequestDto {
  @NotBlank
  private String refreshToken;
}
