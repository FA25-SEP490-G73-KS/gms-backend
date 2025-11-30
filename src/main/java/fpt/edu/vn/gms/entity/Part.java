package fpt.edu.vn.gms.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

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

    @Column(unique = true, length = 50)
    private String sku;

    @Column(name = "part_name", length = 100, nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private PartCategory category;

    @ManyToOne
    @JoinColumn(name = "vehicle_model")
    private VehicleModel vehicleModel;

    @ManyToOne
    @JoinColumn(name = "market")
    private Market market;

    @Column(name = "purchase_price", precision = 12, scale = 2)
    private BigDecimal purchasePrice;

    @Column(name = "selling_price", precision = 12, scale = 2)
    private BigDecimal sellingPrice;

    @Column(name = "discount_rate", precision = 5, scale = 2)
    private BigDecimal discountRate;

    @Column(name = "quantity_in_stock", columnDefinition = "DOUBLE DEFAULT 0")
    @Builder.Default
    private Double quantityInStock = 0.0;

    @ManyToOne
    @JoinColumn(name = "unit")
    private Unit unit;

    @Column(name = "reserved_quantity", columnDefinition = "DOUBLE DEFAULT 0")
    @Builder.Default
    private Double reservedQuantity = 0.0;

    @Column(name = "reorder_level", columnDefinition = "DOUBLE DEFAULT 0")
    @Builder.Default
    private Double reorderLevel = 0.0;

    @ManyToOne
    @JoinColumn(name = "supplier")
    private Supplier supplier;

    @Column(name = "is_universal", nullable = false)
    private boolean isUniversal = false;

    @Column(nullable = false)
    private boolean specialPart = false;

    @Column(length = 100, nullable = true)
    private String note;

    @OneToMany(mappedBy = "part")
    private Set<PurchaseRequestItem> purchaseRequestItems = new HashSet<>();
}
