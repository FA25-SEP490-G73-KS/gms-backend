package fpt.edu.vn.gms.dto.response;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerDebtResponseDto {

    private Long id;
    private Long serviceTicketId;
    private String serviceTicketCode;
    private String createdAt;
    private BigDecimal totalAmount;
    private BigDecimal paidAmount;
    private String dueDate;
    private String status;
}
