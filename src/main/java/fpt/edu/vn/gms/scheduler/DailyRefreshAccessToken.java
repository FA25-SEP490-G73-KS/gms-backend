package fpt.edu.vn.gms.scheduler;

import fpt.edu.vn.gms.entity.AccessToken;
import fpt.edu.vn.gms.service.zalo.AccessTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class DailyRefreshAccessToken {

    private final AccessTokenService accessTokenService;

    /**
     * Proactive refresh: Chạy mỗi 24 giờ để đảm bảo token luôn fresh
     * Access token của Zalo có thời hạn 25 giờ, refresh sau 24 giờ để an toàn
     */
    @Scheduled(fixedRate = 24 * 60 * 60 * 1000, initialDelay = 60000) // 24 giờ, delay 1 phút sau khi start
    public void dailyRefreshToken() {
        log.info("=== Daily token refresh started ===");

        try {
            // Log token hiện tại trước khi refresh
            AccessToken currentToken = accessTokenService.getAccessToken();
            if (currentToken != null && currentToken.getCreateAt() != null) {
                long ageHours = Duration.between(
                        currentToken.getCreateAt().toInstant(),
                        Instant.now()
                ).toHours();
                log.info("Current token age: {} hours", ageHours);
            }

            // Refresh token
            AccessToken newToken = accessTokenService.refreshAccessToken();
            log.info("✓ Daily token refresh SUCCESS - New token ID: {}", newToken.getId());

        } catch (Exception e) {
            log.error("✗ Daily token refresh FAILED", e);
            // TODO: Send alert to monitoring system (Slack, Email, etc.)
            // alertService.sendCriticalAlert("Daily token refresh failed: " + e.getMessage());
        }

        log.info("=== Daily token refresh completed ===");
    }

    /**
     * Token health monitoring: Chạy mỗi 6 giờ để monitor token health
     * Không refresh, chỉ check và alert nếu có vấn đề
     */
    @Scheduled(fixedRate = 6 * 60 * 60 * 1000, initialDelay = 10 * 60 * 1000) // 6 giờ, delay 10 phút
    public void tokenHealthMonitoring() {
        log.info("=== Token health monitoring started ===");

        try {
            AccessToken token = accessTokenService.getAccessToken();

            if (token == null) {
                log.error("⚠ CRITICAL: No access token found in database!");
                // TODO: Send critical alert
                return;
            }

            if (token.getAccessToken() == null || token.getAccessToken().isEmpty()) {
                log.error("⚠ CRITICAL: Access token is null or empty!");
                // TODO: Send critical alert
                return;
            }

            if (token.getRefreshToken() == null || token.getRefreshToken().isEmpty()) {
                log.error("⚠ CRITICAL: Refresh token is null or empty!");
                // TODO: Send critical alert
                return;
            }

            // Calculate token age
            if (token.getCreateAt() != null) {
                long ageHours = Duration.between(
                        token.getCreateAt().toInstant(),
                        Instant.now()
                ).toHours();

                log.info("Token health report:");
                log.info("  - Token ID: {}", token.getId());
                log.info("  - Token age: {} hours", ageHours);
                log.info("  - Created at: {}", token.getCreateAt());
                log.info("  - Access token length: {}", token.getAccessToken().length());
                log.info("  - Refresh token length: {}", token.getRefreshToken().length());

                // Warning nếu token gần hết hạn (> 24 giờ)
                if (ageHours > 24) {
                    log.warn("⚠ WARNING: Token is {} hours old (>24h)! Should have been refreshed.", ageHours);
                    // TODO: Send warning alert
                }

                // Critical nếu token quá hạn (> 25 giờ)
                if (ageHours >= 25) {
                    log.error("⚠ CRITICAL: Token is {} hours old (>=25h)! Token might be expired!", ageHours);
                    // TODO: Send critical alert and attempt emergency refresh
                    try {
                        log.info("Attempting emergency token refresh...");
                        accessTokenService.refreshAccessToken();
                        log.info("✓ Emergency refresh SUCCESS");
                    } catch (Exception e) {
                        log.error("✗ Emergency refresh FAILED", e);
                    }
                }
            } else {
                log.warn("Token createAt is null, cannot calculate age");
            }

            log.info("✓ Token health check PASSED");

        } catch (Exception e) {
            log.error("✗ Token health monitoring FAILED", e);
        }

        log.info("=== Token health monitoring completed ===");
    }

    /**
     * Refresh token health check: Chạy mỗi tuần để check refresh token expiry
     * Refresh token có hiệu lực 3 tháng, cần monitor để tránh hết hạn
     */
    @Scheduled(cron = "0 0 3 * * MON", zone = "Asia/Ho_Chi_Minh") // 3 AM mỗi thứ 2
    public void weeklyRefreshTokenCheck() {
        log.info("=== Weekly refresh token health check started ===");

        try {
            AccessToken token = accessTokenService.getAccessToken();

            if (token == null || token.getCreateAt() == null) {
                log.error("⚠ Cannot check refresh token age: token or createAt is null");
                return;
            }

            long ageDays = Duration.between(
                    token.getCreateAt().toInstant(),
                    Instant.now()
            ).toDays();

            log.info("Refresh token age: {} days (expires after 90 days)", ageDays);

            // Warning nếu refresh token > 60 ngày (còn 1 tháng)
            if (ageDays > 60) {
                log.warn("⚠ WARNING: Refresh token is {} days old. Expires in {} days.",
                        ageDays, 90 - ageDays);
                // TODO: Send warning to team to prepare for re-authorization
            }

            // Critical nếu refresh token > 80 ngày (còn 10 ngày)
            if (ageDays > 80) {
                log.error("⚠ CRITICAL: Refresh token is {} days old! Only {} days until expiry!",
                        ageDays, 90 - ageDays);
                log.error("ACTION REQUIRED: Need to re-authorize app with OA before refresh token expires!");
                // TODO: Send critical alert to team
            }

        } catch (Exception e) {
            log.error("✗ Weekly refresh token check FAILED", e);
        }

        log.info("=== Weekly refresh token health check completed ===");
    }

    /**
     * Daily comprehensive health report: Chạy mỗi ngày để tổng hợp báo cáo
     */
    @Scheduled(cron = "${job.daily.health-report:0 0 9 * * *}", zone = "Asia/Ho_Chi_Minh") // 9 AM mỗi ngày
    public void dailyHealthReport() {
        log.info("=== Daily comprehensive health report ===");

        try {
            AccessToken token = accessTokenService.getAccessToken();

            if (token == null) {
                log.error("No token found!");
                return;
            }

            if (token.getCreateAt() != null) {
                long ageHours = Duration.between(
                        token.getCreateAt().toInstant(),
                        Instant.now()
                ).toHours();

                long ageDays = Duration.between(
                        token.getCreateAt().toInstant(),
                        Instant.now()
                ).toDays();

                log.info("╔═══════════════════════════════════════════════════╗");
                log.info("║         ZALO TOKEN HEALTH REPORT                  ║");
                log.info("╠═══════════════════════════════════════════════════╣");
                log.info("║ Access Token Age:    {} hours / 25 hours        ║", String.format("%2d", ageHours));
                log.info("║ Refresh Token Age:   {} days / 90 days          ║", String.format("%2d", ageDays));
                log.info("║ Access Token Status: {}                         ║",
                        ageHours < 24 ? "✓ HEALTHY  " : "⚠ WARNING  ");
                log.info("║ Refresh Token Status: {}                        ║",
                        ageDays < 60 ? "✓ HEALTHY  " : ageDays < 80 ? "⚠ WARNING  " : "⚠ CRITICAL ");
                log.info("╚═══════════════════════════════════════════════════╝");

                // TODO: Send daily report to monitoring dashboard or Slack
            }

        } catch (Exception e) {
            log.error("✗ Daily health report FAILED", e);
        }
    }
}