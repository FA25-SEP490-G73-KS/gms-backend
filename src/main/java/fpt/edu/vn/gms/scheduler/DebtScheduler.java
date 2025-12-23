package fpt.edu.vn.gms.scheduler;

import fpt.edu.vn.gms.common.enums.DebtStatus;
import fpt.edu.vn.gms.entity.Debt;
import fpt.edu.vn.gms.repository.DebtRepository;
import fpt.edu.vn.gms.service.zalo.ZnsNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class DebtScheduler {

    private final DebtRepository debtRepository;
    private final ZnsNotificationService znsNotificationService;

    /**
     * Send debt reminder notifications at 7:00 AM for all debts with due_date =
     * today
     * and status = OUTSTANDING.
     * Runs once a day at 7:00 AM (Vietnam time).
     */
    @Scheduled(cron = "0 0 7 * * *", zone = "Asia/Ho_Chi_Minh")
    public void sendTodayDebtReminders() {
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Ho_Chi_Minh"));

        // Lấy tất cả các debt có due_date = hôm nay và status = OUTSTANDING
        List<Debt> debts = debtRepository.findByDueDateAndStatus(today, DebtStatus.OUTSTANDING);

        log.info("Found {} debts with due date today ({}) and status OUTSTANDING, sending reminders...",
                debts.size(), today);

        int successCount = 0;
        int failureCount = 0;

        for (Debt debt : debts) {
            try {
                znsNotificationService.sendDebtNotification(debt);
                successCount++;
                log.info("Sent debt reminder for debt ID: {} (Service Ticket: {})",
                        debt.getId(), debt.getServiceTicket().getServiceTicketCode());
            } catch (Exception e) {
                failureCount++;
                log.error("Failed to send debt reminder for debt ID: {} (Service Ticket: {})",
                        debt.getId(),
                        debt.getServiceTicket() != null ? debt.getServiceTicket().getServiceTicketCode() : "N/A",
                        e);
            }
        }

        log.info("Debt reminder job completed. Success: {}, Failed: {}", successCount, failureCount);
    }
}
