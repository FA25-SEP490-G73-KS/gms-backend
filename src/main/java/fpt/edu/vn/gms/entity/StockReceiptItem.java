package fpt.edu.vn.gms.entity;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_receipt_id", nullable = false)
    private StockReceipt stockReceipt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchase_request_item_id", nullable = false)
    private PurchaseRequestItem purchaseRequestItem;

    @Column(name = "requested_quantity")
    private Double requestedQuantity;      // Số lượng yêu cầu (snapshot)

    @Column(name = "quantity_received")
    private Double quantityReceived;       // Số lượng thực nhận đợt này

    @Column(name = "attachment_url", length = 255)
    private String attachmentUrl;          // File đính kèm (hóa đơn / phiếu giao hàng)

    @Column(name = "note", length = 255)
    private String note;                   // Ghi chú/Lý do

    @Column(name = "received_at", nullable = false)
    private LocalDateTime receivedAt;

    private String receivedByName;
    private Long receivedById;
}
