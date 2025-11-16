package fpt.edu.vn.gms.dto.request;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class WarehouseReviewItemDto {

    private Long itemId;
    private Long partId;
    private boolean confirmed;
    private String warehouseNote;
}
