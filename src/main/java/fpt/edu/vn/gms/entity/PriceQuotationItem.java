package fpt.edu.vn.gms.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "PriceQuotationItem")
public class PriceQuotationItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "price_quotation_item_id")
    private Long quotationItemId;

    @ManyToOne
    @JoinColumn(name = "price_quotation_id", referencedColumnName = "price_quotation_id")
    private PriceQuotation pricequotation;

    @ManyToOne(optional = true)
    @JoinColumn(name = "part_id", referencedColumnName = "part_id")
    private Part part;

    @Column(name = "description", columnDefinition = "text")
    private String description;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "total_price")
    private BigDecimal totalPrice;

    @Column(name = "part_status")
    private String partStatus;

    @Column(name = "update_at")
    private LocalDateTime updateAt;

    @Column(name = "account_id")
    private Integer accountId;

    @Column(name = "update_status", length = 50)
    private String updateStatus;
}
