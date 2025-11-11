package fpt.edu.vn.gms.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PurchaseRequestItemResponseDto {

    private Long itemId;
    private String partName;
    private Integer quantity;
    private String vehicleModel;
    private String origin;
    private String status;
    private String warehouseNote;
}
