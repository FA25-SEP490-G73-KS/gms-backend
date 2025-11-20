package fpt.edu.vn.gms.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class CreatePaymentLinkRequestDto {

  @NotBlank
  private String customerFullName;

  @NotBlank
  private String customerPhone;

  @NotBlank
  private String customerAddress;

  @NotBlank
  private String description;

  @NotNull
  private Long price;
}
