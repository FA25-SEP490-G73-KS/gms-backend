package fpt.edu.vn.gms.entity;

import fpt.edu.vn.gms.common.enums.LedgerVoucherCategory;
import fpt.edu.vn.gms.common.enums.ManualVoucherStatus;
import fpt.edu.vn.gms.common.enums.ManualVoucherType;
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
public class LedgerVoucher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ledger_id")
    private Long id;

    @Column(name = "code", length = 50, nullable = false, unique = true)
    private String code;  // THU-2025-00001 / CHI-2025-00001

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 20)
    private ManualVoucherType type;

    @Column(name = "amount", precision = 18, scale = 2, nullable = false)
    private BigDecimal amount; // tổng tiền chi

    private Long relatedEmployeeId; // nếu chi lương

    private Long relatedSupplierId; // nếu chi NCC

    @Column(name = "description", length = 255)
    private String description; // "Thanh toán vật tư: Dầu máy 5W-30"

    @Enumerated(EnumType.STRING)
    private LedgerVoucherCategory category;
    // ví dụ: SALARY, ELECTRICITY, SUPPLIER_PAYMENT, OTHER

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_employee_id")
    private Employee createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by_employee_id")
    private Employee approvedBy;

    @Column(name = "attachment_url", length = 255)
    private String attachmentUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private ManualVoucherStatus status;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

}
