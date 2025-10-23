package fpt.edu.vn.gms.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "PurchaseRequest")
public class PurchaseRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "purchase_request_id")
    private Long purchaseRequestId;

    @ManyToOne
    @JoinColumn(name = "price_quotation_id", referencedColumnName = "price_quotation_id")

    private PriceQuotation priceQuotation;


    @ManyToOne
    @JoinColumn(name = "part_id", referencedColumnName = "part_id")
    private Part part;

    @Column(name = "supplier", length = 100)
    private String supplier;

    @Column(name = "expected_date")
    private LocalDateTime expectedDate;

    @Column(name = "status", length = 50)
    private String status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
