package fpt.edu.vn.gms.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExpenseDto {
    private Long expenseId;
    private String type;
    private String description;
    private BigDecimal amount;
    private Long employeeId;
    private LocalDateTime expenseDate;
    private LocalDateTime createdAt;
}
