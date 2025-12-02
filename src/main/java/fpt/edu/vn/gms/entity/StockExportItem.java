package fpt.edu.vn.gms.entity;

import fpt.edu.vn.gms.common.enums.ExportItemStatus;
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
    @JoinColumn(name = "quotation_item_id")
    private PriceQuotationItem quotationItem;

    @ManyToOne
    @JoinColumn(name = "part_id", nullable = false)
    private Part part;

    @Column(nullable = false)
    private Double quantity;

    private Double quantityExported;

    @ManyToOne
    @JoinColumn(name = "receiver_id")
    private Employee receiver;

    @Column(name = "exported_at")
    private LocalDateTime exportedAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ExportItemStatus status;

    private String note;
}
