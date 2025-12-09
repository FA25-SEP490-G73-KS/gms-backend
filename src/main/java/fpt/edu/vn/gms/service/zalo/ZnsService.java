package fpt.edu.vn.gms.service.zalo;

import com.google.common.reflect.TypeToken;
import fpt.edu.vn.gms.dto.zalo.*;
import fpt.edu.vn.gms.entity.AccessToken;
import fpt.edu.vn.gms.utils.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;

@Service
@RequiredArgsConstructor
@Slf4j
public class ZnsService {
    @Value("${zalo.send-zns.url}")
    private String sendZnsUrl;
    private final AccessTokenService accessTokenService;

    /**
     * Gửi ZNS với retry logic
     * - Lần 1: Dùng token hiện tại từ DB
     * - Nếu fail do token: refresh và retry 1 lần duy nhất
     */
    public boolean sendZns(SendZnsPayload payload) throws Exception {
        log.info("Sending ZNS: {}", payload);

        // Attempt 1: Dùng token hiện tại
        SendResult firstAttempt = trySend(payload);

        if (firstAttempt.success) {
            return true;
        }

        // Nếu fail do token issue, refresh và retry
        if (firstAttempt.isTokenIssue) {
            log.warn("Token issue detected, refreshing token and retrying...");

            try {
                accessTokenService.refreshAccessToken();
            } catch (Exception e) {
                log.error("Failed to refresh token before retry", e);
                return false;
            }

            // Attempt 2: Retry với token mới
            SendResult secondAttempt = trySend(payload);
            return secondAttempt.success;
        }

        // Fail vì lý do khác (không phải token)
        return false;
    }

    /**
     * Thử gửi ZNS một lần
     */
    private SendResult trySend(SendZnsPayload payload) throws Exception {
        // Lấy token mới nhất từ DB (không cache)
        AccessToken accessToken = accessTokenService.getAccessToken();

        if (accessToken == null || accessToken.getAccessToken() == null || accessToken.getAccessToken().isEmpty()) {
            log.error("Access token is null or empty");
            return SendResult.failure(true); // Token issue
        }

        OkHttpClient okHttpClient = HttpUtils.createInstance();
        Request request = buildRequest(payload, accessToken);
        Response response = okHttpClient.newCall(request).execute();

        if (!response.isSuccessful()) {
            log.warn("ZNS request failed: HTTP {}", response.code());

            // 401 = Unauthorized, thường do token expired
            boolean isTokenIssue = (response.code() == 401);
            return SendResult.failure(isTokenIssue);
        }

        if (response.body() == null) {
            log.error("Response body is null");
            return SendResult.failure(false);
        }

        String body = response.body().string();
        log.info("ZNS response: {}", body);

        Type type = new TypeToken<ZaloMessageResponse<DataInfo>>() {}.getType();
        ZaloMessageResponse<DataInfo> res = GsonUtil.fromJson(body, type);

        if (res.getError() == 0) {
            log.info("ZNS sent successfully");
            return SendResult.success();
        } else {
            log.error("ZNS error: {}", res.getMessage());

            // Kiểm tra xem error có liên quan đến token không
            String msg = res.getMessage() != null ? res.getMessage().toLowerCase() : "";
            boolean isTokenIssue = msg.contains("access token")
                    || msg.contains("token")
                    || msg.contains("expire")
                    || msg.contains("invalid");

            return SendResult.failure(isTokenIssue);
        }
    }

    private Request buildRequest(SendZnsPayload payload, AccessToken token) {
        MediaType mediaType = MediaType.parse("application/json");
        String json = GsonUtil.toJson(payload);
        log.debug("Request URL: {}", sendZnsUrl);
        log.debug("Request body: {}", json);

        RequestBody body = RequestBody.create(mediaType, json);
        return new Request.Builder()
                .url(sendZnsUrl)
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .addHeader("access_token", token.getAccessToken())
                .build();
    }

    /**
     * Inner class để wrap kết quả gửi ZNS
     */
    private static class SendResult {
        final boolean success;
        final boolean isTokenIssue;

        private SendResult(boolean success, boolean isTokenIssue) {
            this.success = success;
            this.isTokenIssue = isTokenIssue;
        }

        static SendResult success() {
            return new SendResult(true, false);
        }

        static SendResult failure(boolean isTokenIssue) {
            return new SendResult(false, isTokenIssue);
        }
    }
}