package fpt.edu.vn.gms.entity;

import fpt.edu.vn.gms.common.PurchaseOrderStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
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

    @Column(unique = true, nullable = false, length = 20)
    private String code;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchase_request_id")
    private PurchaseRequest purchaseRequest;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 30)
    private PurchaseOrderStatus status;

    // Tổng tiền dự kiến của PO
    @Column(nullable = false)
    private BigDecimal totalAmount;

    @OneToMany(mappedBy = "purchaseOrder", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PurchaseOrderItem> items;

    @Column
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime approvedAt;

    @Column
    private LocalDateTime receivedAt; // thời điểm hàng về
}
