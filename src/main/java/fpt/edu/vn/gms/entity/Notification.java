package fpt.edu.vn.gms.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

import fpt.edu.vn.gms.common.enums.NotificationStatus;
import fpt.edu.vn.gms.common.enums.NotificationType;

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

    @ManyToOne
    @JoinColumn(name = "receiver_id")
    private Employee receiver;

    @Enumerated(EnumType.STRING)
    private NotificationType type;

    private String title;

    @Column(length = 500)
    private String message;

    private Long referenceId;
    private String referenceType;

    private String actionPath;

    @Enumerated(EnumType.STRING)
    private NotificationStatus status;

    private LocalDateTime createdAt;
}
