package fpt.edu.vn.gms.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DebtDto {
    private Long debtId;
    private Long customerId;
    private Long serviceTicketId;
    private BigDecimal amountDue;
    private LocalDate dueDate;
    private BigDecimal paidAmount;
    private String status;
    private LocalDateTime updatedAt;
}
