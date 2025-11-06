package fpt.edu.vn.gms.entity;

import fpt.edu.vn.gms.common.ManagerReviewStatus;
import fpt.edu.vn.gms.common.WarehouseReviewStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "purchase_request_item")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurchaseRequestItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "purchase_request_item_id")
    private Long itemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchase_request_id")
    private PurchaseRequest purchaseRequest;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "part_id")
    private Part part;

    @Column(name = "part_name")
    private String partName;

    @Column(name = "quantity")
    private Double quantity;

    @Column(name = "unit")
    private String unit;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 30)
    private ManagerReviewStatus status = ManagerReviewStatus.PENDING;

    @Column(name = "note", length = 255)
    private String note;
}
