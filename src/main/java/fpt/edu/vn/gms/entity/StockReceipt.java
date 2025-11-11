package fpt.edu.vn.gms.entity;

import fpt.edu.vn.gms.common.StockReceiptStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

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
    @Column(name = "stock_receipt_id")
    private Long id;

    @Column(name = "receipt_code", length = 50, unique = true)
    private String receiptCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 30)
    private StockReceiptStatus status = StockReceiptStatus.CREATED;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "received_at")
    private LocalDateTime receivedAt;

    @Column(name = "received_by", length = 100)
    private String receivedBy;

    @OneToMany(mappedBy = "stockReceipt", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<StockReceiptItem> items = new HashSet<>();
}
