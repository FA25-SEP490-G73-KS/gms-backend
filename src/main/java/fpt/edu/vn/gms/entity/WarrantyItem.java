package fpt.edu.vn.gms.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "WarrantyItem")
public class WarrantyItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "warranty_item_id")
    private Long warrantyItemId;

    @ManyToOne
    @JoinColumn(name = "warranty_id", referencedColumnName = "warranty_id")
    private Warranty warranty;

    @ManyToOne(optional = true)
    @JoinColumn(name = "part_id", referencedColumnName = "part_id")
    private Part part;

    @Column(name = "description", columnDefinition = "nvarchar(max)")
    private String description;

    @Column(name = "cost", precision = 18, scale = 2)
    private BigDecimal cost;

    @Column(name = "note", length = 500)
    private String note;
}
