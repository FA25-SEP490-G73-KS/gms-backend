package fpt.edu.vn.gms.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class DeductionDto {

    private String type;
    private BigDecimal amount;
    private LocalDate date;
    private String createdBy;
}
