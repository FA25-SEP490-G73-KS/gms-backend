package fpt.edu.vn.gms.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonthWarehouseCostDto {

    private int year;
    private int month;
    private BigDecimal importCost;
    private BigDecimal exportCost;
}
