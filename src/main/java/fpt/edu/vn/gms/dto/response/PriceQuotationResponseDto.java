package fpt.edu.vn.gms.dto.response;

import fpt.edu.vn.gms.common.PriceQuotationStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class PriceQuotationResponseDto {

    private Long priceQuotationId;
    private String code;
    private String serviceTicketCode;
    private String licensePlate;
    private PriceQuotationStatus status;
    private List<PriceQuotationItemResponseDto> items;
    private BigDecimal estimateAmount;
    private BigDecimal discount;
    private LocalDate createdAt;
}
