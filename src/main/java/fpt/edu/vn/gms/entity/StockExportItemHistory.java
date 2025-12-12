package fpt.edu.vn.gms.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "stock_export_item_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockExportItemHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "export_item_id", nullable = false)
    private StockExportItem stockExportItem;

    @Column(nullable = false)
    private Double quantity;

    @Column(name = "exported_at", nullable = false)
    private LocalDateTime exportedAt;

    @ManyToOne
    @JoinColumn(name = "exported_by")
    private Employee exportedBy;

    @PrePersist
    protected void onCreate() {
        this.exportedAt = LocalDateTime.now();
    }
}

