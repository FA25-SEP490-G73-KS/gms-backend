package fpt.edu.vn.gms.entity;

import fpt.edu.vn.gms.common.PriceQuotationItemStatus;
import fpt.edu.vn.gms.common.PriceQuotationItemType;
import fpt.edu.vn.gms.common.WarehouseReviewStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;


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
    private String itemName; // Cho phép nhập nếu part chưa tồn tại

    @Column(name = "unit_price", precision = 18, scale = 2)
    private BigDecimal unitPrice;

    @Column(name = "quantity")
    private Double quantity;

    @Column(name = "unit", length = 20)
    private String unit;

    @Column(name = "discount_rate", precision = 5, scale = 2)
    private BigDecimal discountRate;

    @Column(name = "total_price", precision = 18, scale = 2)
    private BigDecimal totalPrice;

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
    private String warehouseNote; // ghi chú của kho
}
