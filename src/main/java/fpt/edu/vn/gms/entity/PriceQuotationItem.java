package fpt.edu.vn.gms.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import fpt.edu.vn.gms.common.enums.ExportStatus;
import fpt.edu.vn.gms.common.enums.PriceQuotationItemStatus;
import fpt.edu.vn.gms.common.enums.PriceQuotationItemType;
import fpt.edu.vn.gms.common.enums.WarehouseReviewStatus;

@Entity
@Table(name = "price_quotation_item")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PriceQuotationItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "price_quotation_item_id")
    private Long priceQuotationItemId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quotation_id", referencedColumnName = "price_quotation_id")
    private PriceQuotation priceQuotation;

    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "part_id", referencedColumnName = "part_id")
    private Part part;

    @Column(name = "part_name", length = 100)
    private String itemName;

    @Column(name = "unit_price", precision = 18, scale = 2)
    private BigDecimal unitPrice;

    @Column(name = "quantity")
    private Double quantity;

    @Column(name = "unit", length = 20)
    private String unit;

    @Column(name = "total_price", precision = 18, scale = 2)
    private BigDecimal totalPrice;

    @Column(name = "exported_quantity")
    private Double exportedQuantity;

    @Enumerated(EnumType.STRING)
    @Column(name = "item_type", nullable = false)
    private PriceQuotationItemType itemType;

    // AVAILABLE, LOW_STOCK, UNKNOWN
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private PriceQuotationItemStatus inventoryStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "warehouse_review_status")
    private WarehouseReviewStatus warehouseReviewStatus;
    // PENDING / CONFIRMED / REJECTED

    @Column(name = "warehouse_note", length = 255)
    private String warehouseNote;

    @Enumerated(EnumType.STRING)
    private ExportStatus exportStatus = ExportStatus.NONE;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "updated_by")
    private Long updatedBy;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
