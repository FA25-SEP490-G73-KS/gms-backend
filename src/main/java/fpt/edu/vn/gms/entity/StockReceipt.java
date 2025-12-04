package fpt.edu.vn.gms.entity;

import fpt.edu.vn.gms.common.enums.StockReceiptStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "stock_receipt")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockReceipt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "receipt_id")
    private Long receiptId;

    @Column(name = "code", unique = true, length = 50, nullable = false)
    private String code;

    @OneToOne
    @JoinColumn(name = "purchase_request_id")
    private PurchaseRequest purchaseRequest;

    @ManyToOne
    @JoinColumn(name = "supplier_id")
    private Supplier supplier;

    private String createdBy;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    private String receivedBy;

    @Column(name = "received_at")
    private LocalDateTime receivedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private StockReceiptStatus status;

    @Column(name = "total_amount", precision = 18, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "note", length = 255)
    private String note;

    @OneToMany(mappedBy = "stockReceipt", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<StockReceiptItem> items = new ArrayList<>();

    public BigDecimal getTotalPaid() {
        return items.stream()
                .flatMap(item -> item.getHistories().stream())
                .map(StockReceiptItemHistory::getAmountPaid)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
