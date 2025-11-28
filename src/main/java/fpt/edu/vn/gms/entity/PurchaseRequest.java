package fpt.edu.vn.gms.entity;

import fpt.edu.vn.gms.common.enums.ManagerReviewStatus;
import fpt.edu.vn.gms.common.enums.PurchaseRequestStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
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

    @Column(nullable = false, unique = true, length = 50)
    private String code;

    // Liên kết đến báo giá
    @OneToOne
    @JoinColumn(name = "quotation_id")
    private PriceQuotation relatedQuotation;

    // Tổng dự kiến chi phí
    @Column(nullable = true)
    private BigDecimal totalEstimatedAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 30)
    private PurchaseRequestStatus status = PurchaseRequestStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(name = "review_status", length = 30)
    private ManagerReviewStatus reviewStatus = ManagerReviewStatus.PENDING;

    @Column(nullable = true, length = 255)
    private String reason;

    @OneToMany(mappedBy = "purchaseRequest", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PurchaseRequestItem> items = new ArrayList<>();

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "updated_by")
    private Long updatedBy;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
