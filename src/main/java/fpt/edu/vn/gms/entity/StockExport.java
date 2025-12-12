package fpt.edu.vn.gms.entity;

import fpt.edu.vn.gms.common.enums.ExportStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "stock_export")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockExport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "export_id")
    private Long id;

    @Column(name = "code", unique = true)
    private String code;

    @OneToOne
    @JoinColumn(name = "quotation_id")
    private PriceQuotation quotation;

    @Column(name = "export_reason")
    private String reason;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ExportStatus status;

    private String createdBy;

    private String exportedBy;

    private String approvedBy;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "exported_at")
    private LocalDateTime exportedAt;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @OneToMany(mappedBy = "stockExport", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StockExportItem> exportItems = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
