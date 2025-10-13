package fpt.edu.vn.gms.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryTransactionDto {
    private Long inventoryTransactionId;
    private Long partId;
    private String type;
    private Integer quantity;
    private Long serviceTicketId;
    private Long purchaseRequestId;
    private LocalDateTime createdAt;
}
