package fpt.edu.vn.gms.entity;

import fpt.edu.vn.gms.common.enums.LedgerVoucherCategory;
import fpt.edu.vn.gms.common.enums.LedgerVoucherStatus;
import fpt.edu.vn.gms.common.enums.LedgerVoucherType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "ledger_voucher")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LedgerVoucher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ledger_id")
    private Long id;

    @Column(name = "code", length = 50, nullable = false, unique = true)
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 20)
    private LedgerVoucherType type;

    @Column(name = "amount", precision = 18, scale = 2, nullable = false)
    private BigDecimal amount;

    private Long relatedEmployeeId;

    private Long relatedSupplierId;

    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @ManyToOne
    @JoinColumn(name = "created_by_employee_id")
    private Employee createdBy;

    @ManyToOne
    @JoinColumn(name = "approved_by_employee_id")
    private Employee approvedBy;

    @Column(name = "attachment_url", length = 255)
    private String attachmentUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private LedgerVoucherStatus status;

    @ManyToOne
    @JoinColumn(name = "receipt_history_id")
    private StockReceiptItemHistory receiptHistory;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

}
