package fpt.edu.vn.gms.service.zalo;

import fpt.edu.vn.gms.entity.OneTimeToken;
import fpt.edu.vn.gms.repository.OneTimeTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class OneTimeTokenService {
    private final OneTimeTokenRepository oneTimeTokenRepository;

    @Transactional
    public void saveToken(String token, String expiresAt) {
        this.oneTimeTokenRepository.save(new OneTimeToken(token, expiresAt));
    }

    @Transactional
    public void deleteToken(String token) {
        oneTimeTokenRepository.deleteByToken(token);
    }
}
