package fpt.edu.vn.gms.dto.request;

import fpt.edu.vn.gms.common.enums.AllowanceType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class AllowanceRequestDto {

    @NotNull
    private Long employeeId;

    @NotNull
    private AllowanceType type; // enum

    @NotNull
    @DecimalMin("0.0")
    private BigDecimal amount;
}