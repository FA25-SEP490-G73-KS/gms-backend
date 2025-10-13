package fpt.edu.vn.gms.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PriceQuotationItemDto {
    private Long priceQuotationItemId;
    private Long quotationId;
    private Long partId;
    private String description;
    private Integer quantity;
    private BigDecimal totalPrice;
    private String partStatus;
    private LocalDateTime updateAt;
    private Integer accountId;
    private String updateStatus;
}
