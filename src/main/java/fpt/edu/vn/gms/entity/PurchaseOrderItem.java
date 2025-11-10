package fpt.edu.vn.gms.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "purchase_order_item")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurchaseOrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "purchase_order_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchase_order_id")
    private PurchaseOrder purchaseOrder;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "part_id")
    private Part part;

    @Column(nullable = false)
    private String partName;

    @Column(nullable = false)
    private Double quantity;

    @Column(nullable = false)
    private BigDecimal purchasePrice; // Giá mua dự kiến từ PR

    @Column
    private BigDecimal sellingPrice; // Nếu muốn lưu giá bán (không bắt buộc)

    @Column
    private BigDecimal amount; // quantity * purchasePrice
}
