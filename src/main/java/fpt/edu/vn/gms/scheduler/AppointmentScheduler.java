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
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AppointmentScheduler {

    private final AppointmentRepository appointmentRepo;
    private final ZnsNotificationService znsNotificationService;

    @Scheduled(cron = "0 */5 * * * *", zone = "Asia/Ho_Chi_Minh")
    public void processPendingAppointments() {
        if (!isWithinWorkingHours()) {
            log.debug("Skip processPendingAppointments outside working hours");
            return;
        }

        ZoneId VN_ZONE = ZoneId.of("Asia/Ho_Chi_Minh");

        LocalDate today = LocalDate.now(VN_ZONE);
        LocalDateTime now = LocalDateTime.now(VN_ZONE);

        List<Appointment> pendingToday = appointmentRepo.findByAppointmentDateAndStatusAndReminderSentFalse(
                today,
                AppointmentStatus.PENDING,
                false);

        List<Appointment> toCancel = new ArrayList<>();
        List<Appointment> toUpdate = new ArrayList<>();

        for (Appointment appt : pendingToday) {
            if (appt.getTimeSlot() == null)
                continue;

            LocalDateTime slotStart = LocalDateTime.of(
                    appt.getAppointmentDate(),
                    appt.getTimeSlot().getStartTime());

            long minutesBeforeSlot = java.time.Duration.between(now, slotStart).toMinutes();

            /*
             * ===========================
             * (1) AUTO REMINDER TRƯỚC 2 TIẾNG
             * // * ===========================
             */
            // if (!appt.isReminderSent()
            // && minutesBeforeSlot > 30
            // && minutesBeforeSlot <= 120) {

            // try {
            // znsNotificationService.sendAppointmentReminder(appt);
            // appt.setReminderSent(true);
            // toUpdate.add(appt);

            // log.info("Sent smart reminder for appointment ID: {}",
            // appt.getAppointmentId());
            // } catch (Exception e) {
            // log.error("Failed to send reminder for appointment ID: {}",
            // appt.getAppointmentId(), e);
            // }
            // }

            /*
             * ===========================
             * (2) AUTO CANCEL TRƯỚC 30 PHÚT (0–30 phút)
             * ===========================
             */
            if (appt.getConfirmedAt() == null &&
                    !appt.isReminderSent() && minutesBeforeSlot <= 30 && minutesBeforeSlot >= 0) {
                appt.setStatus(AppointmentStatus.CANCELLED);
                toCancel.add(appt);
            }
        }

        if (!toUpdate.isEmpty()) {
            appointmentRepo.saveAll(toUpdate);
        }

        if (!toCancel.isEmpty()) {
            appointmentRepo.saveAll(toCancel);
            log.info("Auto-cancelled {} appointments (not confirmed)", toCancel.size());
        }
    }

    private boolean isWithinWorkingHours() {
        ZoneId vnZone = ZoneId.of("Asia/Ho_Chi_Minh");
        LocalTime now = LocalTime.now(vnZone);

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
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Ho_Chi_Minh"));

        // Lấy tất cả lịch hẹn hôm nay, trạng thái PENDING
        List<Appointment> appointments = appointmentRepo.findByAppointmentDateAndStatusAndReminderSentFalse(
                today, AppointmentStatus.PENDING, false);

        log.info("Found {} appointments scheduled for today ({}), sending reminders...",
                appointments.size(), today);

        List<Appointment> updatedAppointments = new ArrayList<>();

        for (Appointment appointment : appointments) {
            try {
                znsNotificationService.sendAppointmentReminder(appointment);
                appointment.setReminderSent(true);
                updatedAppointments.add(appointment);
                log.info("Sent reminder for appointment ID: {}", appointment.getAppointmentId());
            } catch (Exception e) {
                log.error("Failed to send reminder for appointment ID: {}",
                        appointment.getAppointmentId(), e);
            }
        }

        // Lưu tất cả appointments đã được update
        if (!updatedAppointments.isEmpty()) {
            appointmentRepo.saveAll(updatedAppointments);
            log.info("Updated reminderSent flag for {} appointments", updatedAppointments.size());
        }
    }

}
