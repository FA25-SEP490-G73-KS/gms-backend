package fpt.edu.vn.gms.entity;

import fpt.edu.vn.gms.common.PartStatus;
import fpt.edu.vn.gms.common.QuotationItemStatus;
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
    private String partName; // Cho phép nhập nếu part chưa tồn tại

    @Column(name = "unit_price", precision = 18, scale = 2)
    private BigDecimal unitPrice;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "discount_rate", precision = 5, scale = 2)
    private BigDecimal discountRate;

    @Column(name = "total_price", precision = 18, scale = 2)
    private BigDecimal totalPrice;

    @Column(name = "description", columnDefinition = "text")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 50)
    private QuotationItemStatus status;

    @PrePersist
    @PreUpdate
    public void calculateTotal() {
        syncStatusWithPart();

        if (unitPrice != null && quantity != null) {
            totalPrice = unitPrice.multiply(BigDecimal.valueOf(quantity));
            if (discountRate != null)
                totalPrice = totalPrice.subtract(totalPrice.multiply(discountRate.divide(BigDecimal.valueOf(100))));
        }
    }

    public void syncStatusWithPart() {
        if (part == null) {
            status = QuotationItemStatus.TEMPORARY;
        } else {
            switch (part.getStatus()) {
                case PartStatus.UNKNOWN -> status = QuotationItemStatus.TEMPORARY;
                case PartStatus.AVAILABLE -> status = QuotationItemStatus.ACTIVE;
                case PartStatus.OUT_OF_STOCK -> status = QuotationItemStatus.OUT_OF_STOCK;
            }
        }
    }
}
