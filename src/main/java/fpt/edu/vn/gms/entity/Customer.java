package fpt.edu.vn.gms.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

import fpt.edu.vn.gms.common.enums.CustomerLoyaltyLevel;
import fpt.edu.vn.gms.common.enums.CustomerType;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "customer")
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

    @Enumerated(EnumType.STRING)
    @Column(name = "customer_type", length = 30)
    private CustomerType customerType;

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Vehicle> vehicles = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "loyalty_level", length = 30)
    private CustomerLoyaltyLevel loyaltyLevel;

    @Column(name = "is_active", nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
    private boolean isActive = true;
}
