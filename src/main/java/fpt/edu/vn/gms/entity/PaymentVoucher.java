package fpt.edu.vn.gms.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "payment_voucher")
public class PaymentVoucher {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // link to PriceQuotation.priceQuotationId
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "price_quotation_id")
    private PriceQuotation quotationId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_ticket_id")
    private ServiceTicket serviceTicketId;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", length = 20)
    private VoucherType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 30)
    private VoucherStatus status;

    @Column(name = "total_amount", precision = 18, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "amount_paid", precision = 18, scale = 2)
    private BigDecimal amountPaid;

    @Column(name = "currency", length = 8)
    private String currency;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // free JSON data
//    @Type(type = "jsonb")
    @Column(columnDefinition = "JSON")
    private String metadata;

    public enum VoucherType { DEPOSIT, FINAL }
    public enum VoucherStatus { DRAFT, PENDING, PAID, PARTIALLY_PAID, CANCELLED, REFUNDED }
}
