package fpt.edu.vn.gms.entity;

import fpt.edu.vn.gms.common.enums.ManagerReviewStatus;
import fpt.edu.vn.gms.common.enums.PurchaseReqItemStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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

    // Liên kết đến báo giá
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quotation_item_id", referencedColumnName = "price_quotation_item_id")
    private PriceQuotationItem quotationItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "part_id")
    private Part part;

    @Column(name = "part_name", nullable = false)
    private String partName;

    @Column(name = "quantity", nullable = false)
    private Double quantity;

    @Column(name = "unit")
    private String unit;

    @Column(nullable = false)
    private BigDecimal estimatedPurchasePrice;

    @Column(name = "quantity_received")
    private Double quantityReceived = 0.0;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 30)
    private PurchaseReqItemStatus status = PurchaseReqItemStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(name = "review_status", length = 30)
    private ManagerReviewStatus reviewStatus = ManagerReviewStatus.PENDING;

    @Column(name = "note", length = 255)
    private String note;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "updated_by")
    private Long updatedBy;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
