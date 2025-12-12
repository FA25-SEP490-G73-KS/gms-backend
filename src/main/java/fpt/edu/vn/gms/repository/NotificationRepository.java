package fpt.edu.vn.gms.repository;

import fpt.edu.vn.gms.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

//    List<Notification> findByReceiver_IdOrderByCreatedAtDesc(Long receiverId);

    List<Notification> findByReceiver_PhoneOrderByCreatedAtDesc(String phone);
}
