package fpt.edu.vn.gms.entity;

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

    // Số lượng nhập trong đợt này
    @Column(nullable = false)
    private Double quantity;

    // Đơn giá nhập thực tế tại thời điểm nhập
    @Column(name = "unit_price", precision = 18, scale = 2)
    private BigDecimal unitPrice;

    // Tổng tiền = quantity * unitPrice
    @Column(name = "total_price", precision = 18, scale = 2)
    private BigDecimal totalPrice;

    // File chứng từ mỗi lần nhập (phiếu giao hàng, hóa đơn…)
    @Column(name = "attachment_url", length = 255)
    private String attachmentUrl;

    // Thời điểm nhập hàng
    @Column(name = "received_at", nullable = false)
    private LocalDateTime receivedAt;

    // Người nhập hàng
    private String receivedBy;

    // Ghi chú cho từng đợt nhập
    @Column(length = 255)
    private String note;

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
