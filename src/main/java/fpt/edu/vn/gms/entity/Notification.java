package fpt.edu.vn.gms.entity;

import fpt.edu.vn.gms.common.NotificationType;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notification")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String message;

    @Column(name = "recipient_phone")
    private String recipientPhone;

    private boolean isRead = false;

    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    private NotificationType type;
}
