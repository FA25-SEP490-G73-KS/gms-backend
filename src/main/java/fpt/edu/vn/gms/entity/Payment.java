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
@Table(name = "payment")
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // link to PriceQuotation.priceQuotationId
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "price_quotation")
    private PriceQuotation quotation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_ticket")
    private ServiceTicket serviceTicket;

    // Tổng tiền hàng
    @Column(name = "item_total", precision = 18, scale = 2)
    private BigDecimal itemTotal;

    // Tiền công
    @Column(name = "labor_cost", precision = 18, scale = 2)
    private BigDecimal laborCost;

    // Chiết khấu (mặc định)
    @Column(name = "discount", precision = 18, scale = 2)
    private BigDecimal discount;

    // Công nợ cũ
    @Column(name = "previous_debt", precision = 18, scale = 2)
    private BigDecimal previousDebt;

    // Tiền cọc đã nhận
    @Column(name = "deposit_received", precision = 18, scale = 2)
    private BigDecimal depositReceived;

    // Tổng số tiền khách cần trả
    @Column(name = "final_amount", precision = 18, scale = 2)
    private BigDecimal finalAmount;

    @Column(length = 8)
    private String currency;

    @Column(columnDefinition = "JSON")
    private String metadata;

    // Hình thức thanh toán
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", length = 30)
    private PaymentMethod paymentMethod;

    // Loại thanh toán
    @Column(name = "payment_type", length = 30)
    private PaymentType paymentType;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Người tạo
    private String createdBy;

    public enum PaymentMethod { CASH, CARD, TRANSFER  }
    public enum PaymentType { PAYMENT, DEPOSIT, FINAL, DEBT, REFUND }
}
