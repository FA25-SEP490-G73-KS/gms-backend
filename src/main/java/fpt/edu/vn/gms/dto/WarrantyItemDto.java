package fpt.edu.vn.gms.dto;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WarrantyItemDto {
    private Long warrantyItemId;
    private Long warrantyId;
    private Long partId;
    private String description;
    private BigDecimal cost;
    private String note;
}
