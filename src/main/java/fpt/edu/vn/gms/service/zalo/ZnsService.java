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
        AccessToken accessToken = accessTokenService.getAccessToken();
        if (accessToken == null) {
            log.error("accessToken is null");
            return false;
        }
        OkHttpClient okHttpClient = HttpUtils.createInstance();
        Request request = buildRequest(payload, accessToken);
        Response response = okHttpClient.newCall(request).execute();
        if (response.isSuccessful()) {
            log.info("Successfully call refresh token");
        } else {
            log.info("Failed to call refresh token");
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
