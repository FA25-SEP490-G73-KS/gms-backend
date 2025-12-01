package fpt.edu.vn.gms.service.impl;

import fpt.edu.vn.gms.entity.OtpVerification;
import fpt.edu.vn.gms.repository.OtpVerificationRepository;
import fpt.edu.vn.gms.service.zalo.ZnsNotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OtpServiceImplTest {

    @Mock
    OtpVerificationRepository otpRepository;
    @Mock
    ZnsNotificationService znsNotificationService;
    @InjectMocks
    OtpServiceImpl service;

    @BeforeEach
    void init() throws Exception {
        // set private fields annotated with @Value using reflection
        java.lang.reflect.Field templateField =
                OtpServiceImpl.class.getDeclaredField("otpTemplateId");
        templateField.setAccessible(true);
        templateField.set(service, "TEMPLATE_ID");

        java.lang.reflect.Field expField =
                OtpServiceImpl.class.getDeclaredField("expirationMinutes");
        expField.setAccessible(true);
        expField.set(service, 5);
    }

    @Test
    void generateAndSendOtp_ShouldSaveOtpAndSendNotification() throws Exception {
        String phone = "0909000000";
        String purpose = "TEST";

        String otp = service.generateAndSendOtp(phone, purpose);

        assertNotNull(otp);
        assertEquals(6, otp.length());

        verify(otpRepository).save(argThat(entity ->
                entity.getPhone().equals(phone)
                        && entity.getOtpCode().equals(otp)
                        && !entity.getIsVerified()
                        && entity.getPurpose().equals(purpose)
        ));

        verify(znsNotificationService).sendOtpNotification(eq(phone), eq(otp), anyString());
    }

    @Test
    void generateAndSendOtp_ShouldStillReturnOtp_WhenNotificationFails() throws Exception {
        String phone = "0909000000";

        doThrow(new RuntimeException("Zalo error"))
                .when(znsNotificationService)
                .sendOtpNotification(anyString(), anyString(), anyString());

        String otp = service.generateAndSendOtp(phone, "TEST");

        assertNotNull(otp);
        verify(otpRepository).save(any(OtpVerification.class));
        verify(znsNotificationService).sendOtpNotification(eq(phone), eq(otp), anyString());
    }

    @Test
    void verifyOtp_ShouldReturnFalse_WhenOtpNotFoundOrExpired() {
        when(otpRepository.findByPhoneAndOtpCodeAndIsVerifiedFalseAndExpiresAtAfter(
                anyString(), anyString(), any(LocalDateTime.class)))
                .thenReturn(Optional.empty());

        boolean result = service.verifyOtp("0909", "000000", "TEST");

        assertFalse(result);
        verify(otpRepository, never()).save(any());
    }

    @Test
    void verifyOtp_ShouldReturnFalse_WhenPurposeMismatch() {
        OtpVerification otp = OtpVerification.builder()
                .otpId(1L)
                .phone("0909")
                .otpCode("123456")
                .expiresAt(LocalDateTime.now().plusMinutes(5))
                .isVerified(false)
                .purpose("OTHER")
                .build();

        when(otpRepository.findByPhoneAndOtpCodeAndIsVerifiedFalseAndExpiresAtAfter(
                eq("0909"), eq("123456"), any(LocalDateTime.class)))
                .thenReturn(Optional.of(otp));

        boolean result = service.verifyOtp("0909", "123456", "TEST");

        assertFalse(result);
        verify(otpRepository, never()).save(any());
        verify(otpRepository, never()).markAsVerified(anyString(), anyString());
    }

    @Test
    void verifyOtp_ShouldMarkVerifiedAndInvalidateOthers_WhenValid() {
        OtpVerification otp = OtpVerification.builder()
                .otpId(1L)
                .phone("0909")
                .otpCode("123456")
                .expiresAt(LocalDateTime.now().plusMinutes(5))
                .isVerified(false)
                .purpose("TEST")
                .build();

        when(otpRepository.findByPhoneAndOtpCodeAndIsVerifiedFalseAndExpiresAtAfter(
                eq("0909"), eq("123456"), any(LocalDateTime.class)))
                .thenReturn(Optional.of(otp));

        boolean result = service.verifyOtp("0909", "123456", "TEST");

        assertTrue(result);
        assertTrue(otp.getIsVerified());

        verify(otpRepository).save(otp);
        verify(otpRepository).markAsVerified("0909", "TEST");
    }
}


