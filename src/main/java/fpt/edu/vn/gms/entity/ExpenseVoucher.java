package fpt.edu.vn.gms.entity;

import fpt.edu.vn.gms.common.enums.ExpenseVoucherStatus;
import fpt.edu.vn.gms.common.enums.ExpenseVoucherType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "expense_voucher")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExpenseVoucher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "expense_voucher_id")
    private Long id;

    @Column(name = "code", length = 50, nullable = false, unique = true)
    private String code; // PC-2025-00001

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 20)
    private ExpenseVoucherType type;  // NCC, LUONG, KHAC

    @Column(name = "amount", precision = 18, scale = 2, nullable = false)
    private BigDecimal amount; // tổng tiền chi

    @Column(name = "target", length = 100)
    private String target; // VD: "NCC"

    @Column(name = "description", length = 255)
    private String description; // "Thanh toán vật tư: Dầu máy 5W-30"

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_employee_id")
    private Employee createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by_employee_id")
    private Employee approvedBy;

    @Column(name = "attachment_url", length = 255)
    private String attachmentUrl; // file chứng từ (optional)

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private ExpenseVoucherStatus status;

    // Một ExpenseVoucher gắn với đúng một StockReceiptItem
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_receipt_item_id", unique = true)
    private StockReceiptItem stockReceiptItem;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
