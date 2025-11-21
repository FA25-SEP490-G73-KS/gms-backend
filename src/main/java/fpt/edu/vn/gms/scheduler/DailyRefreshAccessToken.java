package fpt.edu.vn.gms.scheduler;

import fpt.edu.vn.gms.service.zalo.AccessTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DailyRefreshAccessToken {

    private final AccessTokenService accessTokenService;

    @Scheduled(cron="${job.daily.refresh-token}", zone = "Asia/Ho_Chi_Minh")
    public void DailyRefreshAccessTokenJob() {
        log.info("dailyRefreshAccessTokenJob start");
        try {
            accessTokenService.refreshAccessToken();
        } catch (Exception e) {
            log.error("dailyRefreshAccessTokenJob exception", e);
        }
        log.info("dailyRefreshAccessTokenJob stop");
    }
}
