package fpt.edu.vn.gms.service.zalo;

import fpt.edu.vn.gms.entity.AccessToken;
import fpt.edu.vn.gms.repository.AccessTokenRepo;
import fpt.edu.vn.gms.utils.GsonUtil;
import fpt.edu.vn.gms.utils.HttpUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

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

    @CacheEvict(value = "zalo_access_token_cache", allEntries = true)
    public AccessToken refreshAccessToken() throws Exception {
        AccessToken current = accessTokenRepo.findTopByOrderByIdDesc();
        log.info("Refresh access token");
        try {
            Request request = buildRequest(current);
            OkHttpClient okHttpClient = HttpUtils.createInstance();
            Response response = okHttpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                log.info("Successfully call refresh token");
            } else {
                log.info("Failed to call refresh token");
                throw new Exception(response.code() + " " + response.message());
            }
            if (response.body() != null) {
                String body = response.body().string();
                log.info("Response body: {}", body);
                AccessToken accessToken = GsonUtil.fromJson(body, AccessToken.class);
                accessTokenRepo.save(accessToken);
                return accessToken;
            } else {
                log.info("Response body is null");
                throw new Exception("Response body is null");
            }
        } catch (Exception e) {
            log.error("Error calling refresh token", e);
            throw new Exception("Error calling refresh token " + e.getMessage());
        }
    }

    private Request buildRequest(AccessToken current) {
        MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
        String content = String.format("refresh_token=%s&app_id=%s&grant_type=refresh_token",
                current.getRefreshToken(), appId);
        log.info("Refresh request content: {}", content);
        RequestBody body = RequestBody.create(mediaType, content);
        return new Request.Builder()
                .url(accessTokenUrl)
                .method("POST", body)
                .addHeader("Content-Type", "application/x-www-form-urlencoded")
                .addHeader("secret_key", appSecret)
                .build();
    }

    @Cacheable("zalo_access_token_cache")
    public AccessToken getAccessToken(boolean refresh) throws Exception {
        if (refresh) {
            return refreshAccessToken();
        }
        return accessTokenRepo.findTopByOrderByIdDesc();
    }

    @Cacheable("zalo_access_token_cache")
    public AccessToken getAccessToken() throws Exception {
        return getAccessToken(false);
    }
}
