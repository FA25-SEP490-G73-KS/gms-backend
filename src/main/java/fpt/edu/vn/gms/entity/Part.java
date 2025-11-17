package fpt.edu.vn.gms.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import fpt.edu.vn.gms.common.enums.Market;

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

    @Column(name = "part_name", length = 100, nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id")
    private PartCategory category;

    @ManyToMany
    @JoinTable(
            name = "part_vehicle_model",
            joinColumns = @JoinColumn(name = "part_id"),
            inverseJoinColumns = @JoinColumn(name = "vehicle_model_id")
    )
    private Set<VehicleModel> compatibleVehicles = new HashSet<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "market", length = 20)
    private Market market;

    @Column(name = "is_universal", nullable = false)
    private boolean isUniversal = false;

    @Column(name = "purchase_price", precision = 12, scale = 2)
    private BigDecimal purchasePrice;

    @Column(name = "selling_price", precision = 12, scale = 2)
    private BigDecimal sellingPrice;

    @Column(name = "discount_rate", precision = 5, scale = 2)
    private BigDecimal discountRate;

    @Column(name = "quantity_in_stock")
    private Double quantityInStock;

    @Column(name = "unit", length = 20)
    private String unit; // đơn vị đo, ví dụ: "chai", "bộ", "lít", "cái"

    @Column(name = "reserved_quantity")
    private Double reservedQuantity;

    @Column(name = "reorder_level")
    private Double reorderLevel;

    @Column(nullable = false)
    private boolean specialPart = false;

    @OneToMany(mappedBy = "part", fetch = FetchType.LAZY)
    private Set<PurchaseRequestItem> purchaseRequestItems = new HashSet<>();
}
