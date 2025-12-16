package fpt.edu.vn.gms.service.zalo;

import fpt.edu.vn.gms.entity.OneTimeToken;
import fpt.edu.vn.gms.repository.OneTimeTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OneTimeTokenService {
    private final OneTimeTokenRepository oneTimeTokenRepository;

    @Transactional
    public OneTimeToken saveToken(String token, String expiresAt) {
        OneTimeToken entity = new OneTimeToken();
        entity.setToken(token);
        entity.setExpiresAt(expiresAt);
        return this.oneTimeTokenRepository.save(entity);
    }

    @Transactional
    public OneTimeToken saveToken(String token, Long secondsToAdd) {
        String expiresAt = java.time.Instant.now().plusSeconds(secondsToAdd).toString();
        OneTimeToken entity = new OneTimeToken();
        entity.setToken(token);
        entity.setExpiresAt(expiresAt);
        return this.oneTimeTokenRepository.save(entity);
    }

    @Transactional
    public void deleteToken(String token) {
        oneTimeTokenRepository.deleteByToken(token);
    }

    public Optional<OneTimeToken> getToken(UUID ottId) {
        return this.oneTimeTokenRepository.findById(ottId);
    }
}