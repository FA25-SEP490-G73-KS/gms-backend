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
     * Strategy: Xóa token cũ, thêm token mới (luôn giữ 1 record duy nhất)
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

            // Lưu lại ID của token cũ để xóa sau
            Long oldTokenId = latest.getId();
            log.info("Current token ID to be replaced: {}", oldTokenId);

            // Gọi Zalo API để lấy token mới
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

            // Parse token mới từ response
            AccessToken newToken = GsonUtil.fromJson(body, AccessToken.class);

            if (newToken.getAccessToken() == null || newToken.getAccessToken().isEmpty()
                    || newToken.getRefreshToken() == null || newToken.getRefreshToken().isEmpty()) {
                throw new Exception("Parsed token is empty");
            }

            // STRATEGY: Xóa cũ, thêm mới
            // Bước 1: Xóa token cũ
            accessTokenRepo.deleteById(oldTokenId);
            log.info("✓ Deleted old token ID: {}", oldTokenId);

            // Bước 2: Lưu token mới (INSERT)
            AccessToken savedToken = accessTokenRepo.save(newToken);
            log.info("✓ Saved new token ID: {}", savedToken.getId());
            log.info("Successfully refreshed access token - Table now has 1 record only");

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
     * Luôn chỉ có 1 record duy nhất trong bảng
     */
    public AccessToken getAccessToken() {
        return accessTokenRepo.findTopByOrderByIdDesc();
    }
}