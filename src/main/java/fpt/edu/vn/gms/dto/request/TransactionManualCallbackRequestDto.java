package fpt.edu.vn.gms.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TransactionManualCallbackRequestDto {

    @NotBlank
    private String paymentLinkId;
}
