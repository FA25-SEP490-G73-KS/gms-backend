package fpt.edu.vn.gms.dto.response.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceTypeExpenseDto {
    private String serviceTypeName;
    private BigDecimal amount;
    private Double percentage;
}

