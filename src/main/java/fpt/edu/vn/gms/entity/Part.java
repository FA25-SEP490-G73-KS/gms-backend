package fpt.edu.vn.gms.entity;

import fpt.edu.vn.gms.common.PartStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "part")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Part {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "part_id")
    private Long partId;

    @Column(name = "part_code", length = 50, unique = true, nullable = true)
    private String partCode;

    @Column(name = "part_name", length = 100)
    private String name;

    @Column(name = "supplier", length = 100)
    private String supplier;

    @Column(name = "unit_price", precision = 18, scale = 2)
    private BigDecimal unitPrice;

    @Column(name = "discount_rate", precision = 5, scale = 2)
    private BigDecimal discountRate;

    @Column(name = "quantity_in_stock")
    private Integer quantityInStock;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 50)
    private PartStatus status;
    // AVAILABLE, OUT_OF_STOCK, UNKNOWN

    @Column(name = "reorder_level")
    private Integer reorderLevel;

    @Column(name = "last_updated")
    private LocalDateTime lastUpdated;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", referencedColumnName = "category_id")
    private Category category;

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        lastUpdated = LocalDateTime.now();
    }
}

