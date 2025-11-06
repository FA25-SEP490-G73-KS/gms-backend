package fpt.edu.vn.gms.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "part_reservation")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PartReservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reservation_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "part_id", nullable = false)
    private Part part;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quotation_item_id", nullable = false)
    private PriceQuotationItem quotationItem;

    @Column(name = "reserved_quantity", nullable = false)
    private Double reservedQuantity;

    @Column(name = "reserved_at")
    private LocalDateTime reservedAt;

    @Column(name = "active")
    private Boolean active = true;
}
