package fpt.edu.vn.gms.scheduler;

import fpt.edu.vn.gms.common.enums.AppointmentStatus;
import fpt.edu.vn.gms.entity.Appointment;
import fpt.edu.vn.gms.repository.AppointmentRepository;
import fpt.edu.vn.gms.service.zalo.ZnsNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AppointmentScheduler {

    private final AppointmentRepository appointmentRepo;
    private final ZnsNotificationService znsNotificationService;


    @Scheduled(cron = "0 */5 * * * *")
    public void cancelUnconfirmedBeforeSlot() {
        if (!isWithinWorkingHours()) {
            log.debug("Skip cancelUnconfirmedBeforeSlot outside working hours");
            return;
        }

        LocalDate today = LocalDate.now();
        LocalDateTime now = LocalDateTime.now();

        List<Appointment> pendingToday =
                appointmentRepo.findByStatusAndConfirmedAtIsNullAndAppointmentDate(
                        AppointmentStatus.PENDING,
                        today
                );

        List<Appointment> toCancel = new ArrayList<>();

        for (Appointment a : pendingToday) {
            if (a.getTimeSlot() == null) continue;

            LocalDateTime slotStart = LocalDateTime.of(
                    a.getAppointmentDate(),
                    a.getTimeSlot().getStartTime());

            if (!now.isBefore(slotStart.minusHours(1))) {
                a.setStatus(AppointmentStatus.CANCELLED);
                toCancel.add(a);
            }
        }

        if (!toCancel.isEmpty()) {
            appointmentRepo.saveAll(toCancel);
            log.info("Auto-cancelled {} appointments (not confirmed)", toCancel.size());
        }
    }

    private boolean isWithinWorkingHours() {
        LocalTime now = LocalTime.now();
        LocalTime start = LocalTime.of(6, 0);
        LocalTime end = LocalTime.of(22, 0);
        return !now.isBefore(start) && now.isBefore(end);
    }

    /**
     * Send appointment reminders at 6:30 AM on the day of the appointment.
     * Runs once a day at 6:30 AM (Vietnam time).
     */
    @Scheduled(cron = "0 30 6 * * *", zone = "Asia/Ho_Chi_Minh")
    public void sendTodayAppointmentReminders() {
        LocalDate today = LocalDate.now();

        // Lấy tất cả lịch hẹn hôm nay, trạng thái PENDING
        List<Appointment> appointments = appointmentRepo.findByAppointmentDateAndStatus(
                today, AppointmentStatus.PENDING);

        log.info("Found {} appointments scheduled for today ({}), sending reminders...",
                appointments.size(), today);

        for (Appointment appointment : appointments) {
            try {
                znsNotificationService.sendAppointmentReminder(appointment);
                log.info("Sent reminder for appointment ID: {}", appointment.getAppointmentId());
            } catch (Exception e) {
                log.error("Failed to send reminder for appointment ID: {}",
                        appointment.getAppointmentId(), e);
            }
        }
    }

}
