package fpt.edu.vn.gms.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PartDto {
    private Long partId;
    private String name;
    private String supplier;
    private BigDecimal costPrice;
    private BigDecimal sellPrice;
    private Integer quantityInStock;
    private String status;
    private Integer reorderLevel;
    private LocalDateTime lastUpdated;
    private Long categoryId;
}
