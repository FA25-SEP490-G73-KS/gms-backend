package fpt.edu.vn.gms.scheduler;

import fpt.edu.vn.gms.common.AppointmentStatus;
import fpt.edu.vn.gms.entity.Appointment;
import fpt.edu.vn.gms.repository.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AppointmentScheduler {

    private final AppointmentRepository appointmentRepo;

    // Chạy mỗi 5 phút (thay đổi cron nếu muốn)
    @Scheduled(cron = "0 */5 * * * *")
    public void markOverdue() {
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();
        List<Appointment> overdue = appointmentRepo.findOverdueAppointments(today, now);

        if (!overdue.isEmpty()) {
            overdue.forEach(a -> {
                // chỉ update những appointment vẫn ở trạng thái CONFIRMED
                if (a.getStatus() == AppointmentStatus.CONFIRMED) {
                    a.setStatus(AppointmentStatus.OVERDUE);
                }
            });
            appointmentRepo.saveAll(overdue);
        }
    }
}
