package fpt.edu.vn.gms.entity;

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

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "stockExport", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StockExportItem> exportItems = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
