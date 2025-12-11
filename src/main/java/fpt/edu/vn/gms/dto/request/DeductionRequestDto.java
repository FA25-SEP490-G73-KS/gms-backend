package fpt.edu.vn.gms.dto.request;

import fpt.edu.vn.gms.common.enums.DeductionType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class DeductionRequestDto {

    @NotNull
    private Long employeeId;

    @NotNull
    private DeductionType type;

    @NotBlank
    private String content; // mô tả lý do

    @NotNull
    @DecimalMin("0.0")
    private BigDecimal amount;

    @NotNull
    private Integer month;

    @NotNull
    private Integer year;
}
