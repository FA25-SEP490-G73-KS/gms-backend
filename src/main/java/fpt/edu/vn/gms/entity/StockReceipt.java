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
    private String code;                       // SR-2025-00001

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchase_request_id", nullable = false)
    private PurchaseRequest purchaseRequest;   // PR liên quan

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee createdBy;                // Thủ kho nhập

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private StockReceiptStatus status;         // CREATED / COMPLETED

    @Column(name = "total_amount", precision = 18, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "note", length = 255)
    private String note;

    @OneToMany(mappedBy = "stockReceipt", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<StockReceiptItem> items = new ArrayList<>();
}
