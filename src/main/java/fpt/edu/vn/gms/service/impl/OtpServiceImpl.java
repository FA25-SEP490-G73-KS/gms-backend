package fpt.edu.vn.gms.service.impl;

import fpt.edu.vn.gms.entity.OtpVerification;
import fpt.edu.vn.gms.repository.OtpVerificationRepository;
import fpt.edu.vn.gms.service.OtpService;
import fpt.edu.vn.gms.service.zalo.ZnsNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class OtpServiceImpl implements OtpService {

    private final OtpVerificationRepository otpRepository;
    private final ZnsNotificationService znsNotificationService;

    @Value("${zalo.otp.template-id:}")
    private String otpTemplateId;

    @Value("${otp.expiration-minutes:5}")
    private int expirationMinutes;

    @Override
    @Transactional
    public String generateAndSendOtp(String phone, String purpose) {
        // Generate 6-digit OTP
        // Generate secure OTP
        SecureRandom random = new SecureRandom();
        String otpCode = String.format("%06d", random.nextInt(1_000_000));


        // Calculate expiration time
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(expirationMinutes);

        // Save OTP to database
        OtpVerification otp = OtpVerification.builder()
                .phone(phone)
                .otpCode(otpCode)
                .expiresAt(expiresAt)
                .isVerified(false)
                .purpose(purpose)
                .build();

        otpRepository.save(otp);

        // Delete all older OTPs of this phone except the new one
        otpRepository.deleteOldOtps(phone, otp.getOtpId());


        // Send OTP via ZNS
        try {
            znsNotificationService.sendOtpNotification(phone, otpCode, otpTemplateId);
            log.info("OTP sent successfully to phone: {}", phone);
        } catch (Exception e) {
            log.error("Failed to send OTP via ZNS to phone: {}", phone, e);
            // Still return OTP for development/testing, but in production might want to throw exception

            // rollback whole transaction
            throw new RuntimeException("Cannot send OTP at the moment");
        }

        return otpCode; // only for dev mode
    }

    @Override
    @Transactional
    public boolean verifyOtp(String phone, String otpCode, String purpose) {
        LocalDateTime now = LocalDateTime.now();

        Optional<OtpVerification> otpOpt = otpRepository.findByPhoneAndOtpCodeAndIsVerifiedFalseAndExpiresAtAfter(
                phone, otpCode, now);

        if (otpOpt.isEmpty()) {
            log.warn("Invalid or expired OTP for phone: {}", phone);
            return false;
        }

        OtpVerification otp = otpOpt.get();
        
        // Check if purpose matches
        if (!purpose.equals(otp.getPurpose())) {
            log.warn("OTP purpose mismatch for phone: {}", phone);
            return false;
        }

        // Mark as verified
        otp.setIsVerified(true);
        otpRepository.save(otp);

        // Mark all other OTPs for this phone and purpose as verified (invalidate them)
        otpRepository.markAsVerified(phone, purpose);

        log.info("OTP verified successfully for phone: {}", phone);
        return true;
    }
}

