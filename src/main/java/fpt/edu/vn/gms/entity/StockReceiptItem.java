package fpt.edu.vn.gms.entity;

import fpt.edu.vn.gms.common.enums.StockReceiptStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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

    @ManyToOne
    @JoinColumn(name = "stock_receipt_id", nullable = false)
    private StockReceipt stockReceipt;

    @ManyToOne
    @JoinColumn(name = "purchase_request_item_id")
    private PurchaseRequestItem purchaseRequestItem;

    @Column(name = "requested_quantity")
    private Double requestedQuantity;

    @Column(name = "quantity_received")
    private Double quantityReceived;

    @Column(name = "actual_unit_price", precision = 18, scale = 2)
    private BigDecimal actualUnitPrice;

    @Column(name = "actual_total_price", precision = 18, scale = 2)
    private BigDecimal actualTotalPrice;

    @Column(name = "attachment_url", length = 255)
    private String attachmentUrl;

    @Column(name = "note", length = 255)
    private String note;

    @Column(name = "received_at")
    private LocalDateTime receivedAt;

    private String receivedBy;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private StockReceiptStatus status;
}
