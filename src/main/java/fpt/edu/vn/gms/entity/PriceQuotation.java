package fpt.edu.vn.gms.entity;

import fpt.edu.vn.gms.common.PriceQuotationStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "price_quotation")
public class PriceQuotation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "price_quotation_id")
    private Long priceQuotationId;

    @OneToOne(mappedBy = "priceQuotation", fetch = FetchType.LAZY)
    private ServiceTicket serviceTicket;

    @Column(name = "estimate_amount", precision = 18, scale = 2)
    private BigDecimal estimateAmount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "discount_policy_id")
    private DiscountPolicy discountPolicy;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private PriceQuotationStatus status = PriceQuotationStatus.DRAFT;

    @OneToMany(mappedBy = "priceQuotation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PriceQuotationItem> items = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {updatedAt = LocalDateTime.now();}

}
