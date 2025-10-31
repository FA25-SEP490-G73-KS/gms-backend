package fpt.edu.vn.gms.entity;

import fpt.edu.vn.gms.common.PurchaseOrderStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "purchase_order")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurchaseOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "purchase_order_id")
    private Long id;

    @Column(name = "order_code", unique = true, length = 50)
    private String orderCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 30)
    private PurchaseOrderStatus status = PurchaseOrderStatus.PENDING;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "purchaseOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<PurchaseOrderItem> items = new HashSet<>();
}
