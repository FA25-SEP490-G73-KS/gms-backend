package fpt.edu.vn.gms.entity;

import fpt.edu.vn.gms.common.PurchaseRequestStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "purchase_request")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurchaseRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "purchase_request_id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 30)
    private PurchaseRequestStatus status = PurchaseRequestStatus.PENDING;

    @ManyToOne
    @JoinColumn(name = "quotation_id")
    private PriceQuotation quotation;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Column(name = "created_by", length = 100)
    private String createdBy;

    @OneToMany(mappedBy = "purchaseRequest", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PurchaseRequestItem> items = new ArrayList<>();
}
