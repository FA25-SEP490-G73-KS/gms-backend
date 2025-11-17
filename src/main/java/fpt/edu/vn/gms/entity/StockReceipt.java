package fpt.edu.vn.gms.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import fpt.edu.vn.gms.common.enums.StockReceiptStatus;

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
    private Long receiptId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "purchase_request_id")
    private PurchaseRequest purchaseRequest;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id")
    private Employee createdBy;

    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "stockReceipt", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<StockReceiptItem> items = new ArrayList<>();
}
