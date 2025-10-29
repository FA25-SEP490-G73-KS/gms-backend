package fpt.edu.vn.gms.entity;

import fpt.edu.vn.gms.common.PartStatus;
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
}
