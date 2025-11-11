package fpt.edu.vn.gms.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class PurchaseRequestResponseDto {

    private Long purchaseRequestId;
    private VehicleResponseDto vehicle;
    private String createdBy;
    private LocalDateTime createdAt;
    private String status;
    private List<PurchaseRequestItemResponseDto> items;
}
