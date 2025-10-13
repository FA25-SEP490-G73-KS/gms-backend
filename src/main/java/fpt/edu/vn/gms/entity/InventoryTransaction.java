package fpt.edu.vn.gms.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "InventoryTransaction")
public class InventoryTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inventory_transaction_id")
    private Long inventoryTransactionId;

    @ManyToOne
    @JoinColumn(name = "part_id", referencedColumnName = "part_id")
    private Part part;

    @Column(name = "type", length = 30)
    private String type;

    @Column(name = "quantity")
    private Integer quantity;

    @ManyToOne(optional = true)
    @JoinColumn(name = "service_ticket_id", referencedColumnName = "service_ticket_id")
    private ServiceTicket serviceTicket;

    @ManyToOne(optional = true)
    @JoinColumn(name = "purchase_request_id", referencedColumnName = "purchase_request_id")
    private PurchaseRequest purchaseRequest;

    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
