package fpt.edu.vn.gms.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import fpt.edu.vn.gms.entity.VehicleModel;

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

    // SKU có thể sinh tự động, vd: "BRAKEPAD-CIVIC"
    @Column(name = "sku", unique = true, length = 50)
    private String sku;

    // Một linh kiện có thể dùng cho nhiều model xe
    @ManyToMany
    @JoinTable(
            name = "part_vehicle_model",
            joinColumns = @JoinColumn(name = "part_id"),
            inverseJoinColumns = @JoinColumn(name = "vehicle_model_id")
    )
    private Set<VehicleModel> compatibleVehicles = new HashSet<>();

    @Column(name = "is_universal", nullable = false)
    private boolean isUniversal = false; // true = dùng chung cho mọi model

    @Column(name = "purchase_price", precision = 12, scale = 2)
    private BigDecimal purchasePrice;

    @Column(name = "selling_price", precision = 12, scale = 2)
    private BigDecimal sellingPrice;

    @Column(name = "discount_rate", precision = 5, scale = 2)
    private BigDecimal discountRate;

    @Column(name = "quantity_in_stock")
    private Integer quantityInStock;

    @Column(name = "reserved_quantity")
    private Integer reservedQuantity;

    @Column(name = "reorder_level")
    private Integer reorderLevel; // ngưỡng báo cần nhập thêm
}
