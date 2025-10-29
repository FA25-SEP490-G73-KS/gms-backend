package fpt.edu.vn.gms.entity;

import fpt.edu.vn.gms.common.PriceQuotationStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    @OneToMany(mappedBy = "priceQuotation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PriceQuotationItem> items = new ArrayList<>();

    @Column(name = "estimate_amount", precision = 18, scale = 2)
    private BigDecimal estimateAmount;
    
    @Column(name = "discount", precision = 18, scale = 2)
    private BigDecimal discount;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private PriceQuotationStatus status;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {updatedAt = LocalDateTime.now();}

    public void calculateTotal() {
        BigDecimal total = items.stream()
                .map(PriceQuotationItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        this.estimateAmount = total.subtract(discount != null ? discount : BigDecimal.ZERO);
    }
}
