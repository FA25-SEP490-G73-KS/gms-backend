package fpt.edu.vn.gms.entity;

import fpt.edu.vn.gms.common.enums.PaymentTransactionType;
import fpt.edu.vn.gms.common.enums.TransactionMethod;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "transaction")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String paymentLinkId; // id link thanh toán của PayOS

    @ManyToOne
    @JoinColumn(name = "invoice_id")
    private Invoice invoice;

    @ManyToOne
    @JoinColumn(name = "debt_id")
    private Debt debt;

    @Column(nullable = false)
    private String customerFullName;

    @Column(nullable = false)
    private String customerPhone;

    @Enumerated(EnumType.STRING)
    @Column(name = "method", length = 30)
    private TransactionMethod method;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", length = 30)
    private PaymentTransactionType type;

    @Column(name = "amount")
    private Long amount;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column
    private Boolean isActive;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
