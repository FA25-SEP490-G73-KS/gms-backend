package fpt.edu.vn.gms.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "Customer")
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "customer_id")
    private Long customerId;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "phone")
    private String phone;

    @Column(name = "zalo_id", length = 50)
    private String zaloId;

    @Column(name = "address", length = 200)
    private String address;

    @Column(name = "customer_type", length = 30)
    private String customerType;

    @Column(name = "loyalty_level", length = 30)
    private String loyaltyLevel;
}
