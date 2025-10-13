package fpt.edu.vn.gms.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurchaseRequestDto {
    private Long purchaseRequestId;
    private Long quotationId;
    private Long partId;
    private String supplier;
    private LocalDateTime expectedDate;
    private String status;
    private LocalDateTime createdAt;
}
