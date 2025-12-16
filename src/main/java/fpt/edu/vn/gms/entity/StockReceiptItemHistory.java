package fpt.edu.vn.gms.entity;

import fpt.edu.vn.gms.common.enums.ReceiptPaymentStatus;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "stock_receipt_item_history")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockReceiptItemHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "history_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "stock_receipt_item_id", nullable = false)
    private StockReceiptItem stockReceiptItem;

    @Column(nullable = false)
    private Double quantity;

    @Column(name = "unit_price", precision = 18, scale = 2)
    private BigDecimal unitPrice;

    @Column(name = "total_price", precision = 18, scale = 2)
    private BigDecimal totalPrice;

    @Column(name = "attachment_url", length = 255)
    private String attachmentUrl;

    @Column(name = "received_at", nullable = false)
    private LocalDateTime receivedAt;

    private String receivedBy;

    @Column(length = 255)
    private String note;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status")
    @Builder.Default
    private ReceiptPaymentStatus paymentStatus = ReceiptPaymentStatus.UNPAID;

    @Column(name = "amount_paid", precision = 18, scale = 2)
    private BigDecimal amountPaid = BigDecimal.ZERO;

    @Column(name = "payment_attachment")
    private String paymentAttachmentUrl;

    @PrePersist
    protected void onCreate() {
        if (this.receivedAt == null) {
            this.receivedAt = LocalDateTime.now();
        }
        if (unitPrice != null && quantity != null) {
            this.totalPrice = unitPrice.multiply(BigDecimal.valueOf(quantity));
        }
    }
}
