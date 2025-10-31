package fpt.edu.vn.gms.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "stock_receipt_item")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockReceiptItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "stock_receipt_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_receipt_id")
    private StockReceipt stockReceipt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchase_order_item_id")
    private PurchaseOrderItem purchaseOrderItem;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "part_id")
    private Part part;

    @Column(name = "quantity_received")
    private Integer quantityReceived;

    @Column(name = "note", length = 255)
    private String note;
}
