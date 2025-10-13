package fpt.edu.vn.gms.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PriceQuotationDto {
    private Long priceQuotationId;
    private Long serviceTicketId;
    private BigDecimal totalAmount;
    private String status;
    private LocalDateTime createdAt;
}
