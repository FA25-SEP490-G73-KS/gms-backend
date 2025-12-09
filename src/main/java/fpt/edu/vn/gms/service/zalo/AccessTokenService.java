package fpt.edu.vn.gms.service.zalo;

import fpt.edu.vn.gms.entity.AccessToken;
import fpt.edu.vn.gms.repository.AccessTokenRepo;
import fpt.edu.vn.gms.utils.GsonUtil;
import fpt.edu.vn.gms.utils.HttpUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccessTokenService {
    @Value("${zalo.appId}")
    private String appId;
    @Value("${zalo.appSecret}")
    private String appSecret;
    @Value("${zalo.access-token.url}")
    private String accessTokenUrl;
    private final AccessTokenRepo accessTokenRepo;

    // Lock object để đảm bảo chỉ 1 thread refresh tại 1 thời điểm
    private final Object refreshLock = new Object();

    /**
     * Refresh access token với synchronized lock
     * Đảm bảo chỉ 1 thread thực hiện refresh tại một thời điểm
     */
    @Transactional
    public AccessToken refreshAccessToken() throws Exception {
        synchronized (refreshLock) {
            // Double-check: Kiểm tra xem có thread khác vừa refresh chưa
            AccessToken latest = accessTokenRepo.findTopByOrderByIdDesc();

            // Nếu token vừa được refresh trong vài giây qua, không cần refresh nữa
            if (latest != null && latest.getCreateAt() != null) {
                long secondsSinceCreated = java.time.Duration.between(
                        latest.getCreateAt().toInstant(),
                        java.time.Instant.now()
                ).getSeconds();

                if (secondsSinceCreated < 10) { // Token mới được tạo trong 10s qua
                    log.info("Token was just refreshed {}s ago, reusing it", secondsSinceCreated);
                    return latest;
                }
            }

            log.info("Refreshing access token...");

            if (latest == null || latest.getRefreshToken() == null || latest.getRefreshToken().isEmpty()) {
                log.error("No refresh token available");
                throw new Exception("Missing refresh token");
            }

            Request request = buildRequest(latest);
            OkHttpClient okHttpClient = HttpUtils.createInstance();
            Response response = okHttpClient.newCall(request).execute();

            if (!response.isSuccessful()) {
                log.error("Failed to refresh token: HTTP {}", response.code());
                throw new Exception(response.code() + " " + response.message());
            }

            if (response.body() == null) {
                log.error("Response body is null");
                throw new Exception("Response body is null");
            }

            String body = response.body().string();
            log.info("Refresh token response: {}", body);

            // Validate response
            if (!body.contains("\"access_token\"") || !body.contains("\"refresh_token\"")) {
                throw new Exception("Invalid refresh response: " + body);
            }

            if (body.contains("\"error\"") && !body.contains("\"access_token\"")) {
                throw new Exception("Zalo refresh error: " + body);
            }

            AccessToken newToken = GsonUtil.fromJson(body, AccessToken.class);

            if (newToken.getAccessToken() == null || newToken.getAccessToken().isEmpty()
                    || newToken.getRefreshToken() == null || newToken.getRefreshToken().isEmpty()) {
                throw new Exception("Parsed token is empty");
            }

            // Lưu token mới vào DB
            AccessToken savedToken = accessTokenRepo.save(newToken);
            log.info("Successfully refreshed and saved new access token");

            return savedToken;
        }
    }

    private Request buildRequest(AccessToken current) {
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        String content = String.format("refresh_token=%s&app_id=%s&grant_type=refresh_token",
                current.getRefreshToken(), appId);
        RequestBody body = RequestBody.create(mediaType, content);
        return new Request.Builder()
                .url(accessTokenUrl)
                .method("POST", body)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .addHeader("secret_key", appSecret)
                .build();
    }

    /**
     * Lấy access token từ DB (không cache)
     * Query trực tiếp từ database mỗi lần gọi
     */
    public AccessToken getAccessToken() {
        return accessTokenRepo.findTopByOrderByIdDesc();
    }
}