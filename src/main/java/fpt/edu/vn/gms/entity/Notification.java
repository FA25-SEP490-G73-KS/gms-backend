package fpt.edu.vn.gms.entity;

import fpt.edu.vn.gms.common.NotificationStatus;
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

    // Người nhận thông báo (employee)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id")
    private Employee receiver;

    // Loại thông báo
    @Enumerated(EnumType.STRING)
    private NotificationType type;

    // Tiêu đề nổi bật
    private String title;

    // Nội dung ngắn
    @Column(length = 500)
    private String message;

    // Dùng để điều hướng đến màn hình cụ thể
    private Long referenceId;
    private String referenceType;

    // Dùng để điều hướng FE (URL hoăc key)
    private String actionPath;

    // READ, UNREAD
    @Enumerated(EnumType.STRING)
    private NotificationStatus status;

    private LocalDateTime createdAt;
}
