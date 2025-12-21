package fpt.edu.vn.gms.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "purchase_request_quotation")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@IdClass(PurchaseRequestQuotationId.class)
public class PurchaseRequestQuotation {
    
    @Id
    @ManyToOne
    @JoinColumn(name = "purchase_request_id")
    private PurchaseRequest purchaseRequest;
    
    @Id
    @ManyToOne
    @JoinColumn(name = "price_quotation_id")
    private PriceQuotation priceQuotation;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}

