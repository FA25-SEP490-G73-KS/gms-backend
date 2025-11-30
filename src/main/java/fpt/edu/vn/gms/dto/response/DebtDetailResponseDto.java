package fpt.edu.vn.gms.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DebtDetailResponseDto {

    private String customerName;
    private String phone;
    private String licensePlate;
    private String address;

    private List<CustomerDebtResponseDto> debts;

    private BigDecimal totalRemainingAmount;
}
