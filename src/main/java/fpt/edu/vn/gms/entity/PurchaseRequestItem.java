package fpt.edu.vn.gms.entity;

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

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quotation_item_id")
    private PriceQuotationItem quotationItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "part_id")
    private Part part;

    @Column(name = "quantity")
    private Integer quantity;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 30)
    private WarehouseReviewStatus status = WarehouseReviewStatus.PENDING;

    @Column(name = "note", length = 255)
    private String note;
}
