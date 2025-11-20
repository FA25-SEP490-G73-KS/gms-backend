package fpt.edu.vn.gms.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "transaction")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String paymentLinkId; // id link thanh toán của PayOS

    @Column(nullable = false)
    private String customerFullName;

    @Column(nullable = false)
    private String customerPhone;

    @Column(nullable = false)
    private String customerAddress;

    @Enumerated(EnumType.STRING)
    @Column(name = "method", length = 30)
    private Method method;

    @Column(name = "amount")
    private Long amount;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column
    private Boolean isActive;

    @Getter
    @RequiredArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    public static enum Method {
        CASH("CASH"),
        BANK_TRANSFER("BANK_TRANSFER");

        String value;
    }
}
