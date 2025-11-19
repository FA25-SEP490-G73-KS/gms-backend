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

    @ManyToOne
    @JoinColumn(name = "payment_voucher_id")
    private Payment paymentVoucher;

    @Enumerated(EnumType.STRING)
    @Column(name = "method", length = 30)
    private Method method;

    @Column(name = "provider_txn_id")
    private String providerTxnId;

    @Column(name = "amount", precision = 18, scale = 2)
    private BigDecimal amount;

    @Column(name = "customer_phone")
    private String customerPhone;

    @Column(name = "customer_full_name")
    private String customerFullName;

    @Column(name = "customer_address")
    private String customerAddress;

    @Column(columnDefinition = "JSON")
    private String payload;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public enum Method { CASH, BANK_TRANSFER, SEPAY_VIETQR }
}
