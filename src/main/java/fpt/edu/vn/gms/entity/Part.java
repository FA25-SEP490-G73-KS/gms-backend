package fpt.edu.vn.gms.entity;

import fpt.edu.vn.gms.common.PartStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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

    @Column(name = "part_name", length = 100)
    private String name;

    @Column(name = "is_universal")
    private boolean isUniversal;

    @ManyToMany
    @JoinTable(
            name = "part_vehicle_model",
            joinColumns = @JoinColumn(name = "part_id"),
            inverseJoinColumns = @JoinColumn(name = "vehicle_model_id")
    )
    private Set<VehicleModel> compatibleVehicles = new HashSet<>();

    @OneToMany(mappedBy = "part", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PartOrigin> origins = new HashSet<>();

    @Column(name = "purchase_price", precision = 12, scale = 2)
    private BigDecimal purchasePrice;

    @Column(name = "selling_price", precision = 12, scale = 2)
    private BigDecimal sellingPrice;

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

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        lastUpdated = LocalDateTime.now();
    }
}

