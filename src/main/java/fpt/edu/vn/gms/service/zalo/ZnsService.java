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

    public boolean sendZns(SendZnsPayload payload) throws Exception {
        log.info("sendZns payload: {}", payload);

        // First attempt
        boolean firstAttempt = trySend(payload, false);
        if (firstAttempt) {
            return true;
        }

        // If first attempt fails due to token issues, refresh and retry once
        try {
            accessTokenService.refreshAccessToken();
        } catch (Exception e) {
            log.error("Failed to refresh access token before retry", e);
            return false;
        }

        return trySend(payload, true);
    }

    private boolean trySend(SendZnsPayload payload, boolean afterRefresh) throws Exception {
        AccessToken accessToken = accessTokenService.getAccessToken();

        if (accessToken == null || accessToken.getAccessToken() == null || accessToken.getAccessToken().isEmpty()) {
            log.error("Access token entity or token string is null/empty");
            // If first attempt and token missing, let caller refresh and retry
            if (!afterRefresh) {
                return false;
            }
            return false;
        }

        OkHttpClient okHttpClient = HttpUtils.createInstance();

        Request request = buildRequest(payload, accessToken);

        Response response = okHttpClient.newCall(request).execute();

        if (!response.isSuccessful()) {
            log.info("ZNS send request not successful: HTTP {}", response.code());
            // If unauthorized, likely token is invalid/expired
            if (!afterRefresh && response.code() == 401) {
                log.warn("Access token possibly invalid/expired (401). Will refresh and retry.");
                return false;
            }
            return false;
        }

        if (response.body() != null) {
            String body = response.body().string();
            log.info("Response body: {}", body);
            Type type = new TypeToken<ZaloMessageResponse<DataInfo>>() {}.getType();
            ZaloMessageResponse<DataInfo> res = GsonUtil.fromJson(body, type);
            if (res.getError() == 0) {
                return true;
            } else {
                log.error("Error when send zns: {}", res.getMessage());
                // Heuristic: if error message indicates token problem, let caller trigger refresh once
                String msg = res.getMessage() != null ? res.getMessage().toLowerCase() : "";
                boolean tokenIssue = msg.contains("access token") || msg.contains("token") || msg.contains("expire");
                if (!afterRefresh && tokenIssue) {
                    log.warn("Detected token-related error from Zalo response. Will refresh and retry once.");
                    return false;
                }
                return false;
            }
        } else {
            log.info("Response body is null");
            return false;
        }
    }


    private Request buildRequest(SendZnsPayload payload, AccessToken current) {
        MediaType mediaType = MediaType.parse("application/json");
        String json = GsonUtil.toJson(payload);
        log.info("request to: {}", sendZnsUrl);
        log.info("body: {}", json);
        RequestBody body = RequestBody.create(mediaType, GsonUtil.toJson(payload));
        return new Request.Builder()
                .url(sendZnsUrl).method("POST", body)
                .addHeader("Content-Type", "application/json")
                .addHeader("access_token", current.getAccessToken())
                .build();
    }
}
