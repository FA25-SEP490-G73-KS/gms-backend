package fpt.edu.vn.gms.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "stock_export_item")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StockExportItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "export_item_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "export_id", nullable = false)
    private StockExport stockExport;

    @ManyToOne
    @JoinColumn(name = "quotation_item_id", nullable = false)
    private PriceQuotationItem quotationItem;

    @Column(name = "quantity", nullable = false)
    private Double quantity;

    @Column(name = "unit", length = 20)
    private String unit;

    @ManyToOne
    @JoinColumn(name = "receiver_id")
    private Employee receiver;

    @Column(name = "exported_at", nullable = false)
    private LocalDateTime exportedAt;

    @PrePersist
    protected void onExport() {
        this.exportedAt = LocalDateTime.now();
    }
}
