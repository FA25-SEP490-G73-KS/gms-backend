package fpt.edu.vn.gms.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "payment_transaction")
public class PaymentTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "payment_voucher_id")
    private Long paymentVoucherId;

    @Enumerated(EnumType.STRING)
    @Column(name = "method", length = 30)
    private Method method;

    @Column(name = "provider_txn_id")
    private String providerTxnId;

    @Column(name = "amount", precision = 18, scale = 2)
    private BigDecimal amount;

    @Column(name = "status", length = 50)
    private String status;

    @Column(name = "received_by")
    private String receivedBy;

    @Column(name = "received_at")
    private LocalDateTime receivedAt;

//    @Type(type = "jsonb")
    @Column(columnDefinition = "JSON")
    private String payload;

    @Column(name = "idempotency_key")
    private String idempotencyKey;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public enum Method { CASH, BANK_TRANSFER, SEPAY_VIETQR }
}
