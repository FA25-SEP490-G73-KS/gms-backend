package fpt.edu.vn.gms.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

import fpt.edu.vn.gms.common.enums.CustomerLoyaltyLevel;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "discount_policy")
public class DiscountPolicy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "discount_policy_id")
    private Long discountPolicyId;

    @Enumerated(EnumType.STRING)
    @Column(name = "loyalty_level", nullable = false, unique = true)
    private CustomerLoyaltyLevel loyaltyLevel;

    @Column(name = "discount_rate", precision = 5, scale = 2, nullable = false)
    private BigDecimal discountRate; // % giảm giá, ví dụ 5.00 = 5%

    @Column(name = "description", length = 255)
    private String description;
}
