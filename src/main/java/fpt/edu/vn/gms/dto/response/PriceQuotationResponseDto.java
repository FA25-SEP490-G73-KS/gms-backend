package fpt.edu.vn.gms.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class PriceQuotationResponseDto {

    private Long id;
    private Long serviceTicketId;
    private BigDecimal estimateAmount;
    private BigDecimal discount;
    private LocalDateTime createdAt;
    private List<PriceQuotationItemResponseDto> items;
}
