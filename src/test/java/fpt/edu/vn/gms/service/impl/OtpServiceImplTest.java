package fpt.edu.vn.gms.service.impl;

import fpt.edu.vn.gms.base.BaseServiceTest;
import fpt.edu.vn.gms.entity.OtpVerification;
import fpt.edu.vn.gms.repository.OtpVerificationRepository;
import fpt.edu.vn.gms.service.zalo.ZnsNotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class OtpServiceImplTest extends BaseServiceTest {

  @Mock
  private OtpVerificationRepository otpRepository;

  @Mock
  private ZnsNotificationService znsNotificationService;

  @InjectMocks
  private OtpServiceImpl otpServiceImpl;

  @BeforeEach
  void setUp() {
    ReflectionTestUtils.setField(otpServiceImpl, "otpTemplateId", "template-123");
    ReflectionTestUtils.setField(otpServiceImpl, "expirationMinutes", 5);
  }

  @Test
  void generateAndSendOtp_WhenCalled_ShouldSaveOtpAndSendNotification() throws Exception {
    String phone = "0123456789";
    String purpose = "LOGIN";

    ArgumentCaptor<OtpVerification> otpCaptor = ArgumentCaptor.forClass(OtpVerification.class);

    String otpCode = otpServiceImpl.generateAndSendOtp(phone, purpose);

    verify(otpRepository).save(otpCaptor.capture());
    OtpVerification savedOtp = otpCaptor.getValue();
    assertEquals(phone, savedOtp.getPhone());
    assertEquals(purpose, savedOtp.getPurpose());
    assertFalse(savedOtp.getIsVerified());
    assertNotNull(savedOtp.getOtpCode());
    assertEquals(6, savedOtp.getOtpCode().length());

    verify(znsNotificationService).sendOtpNotification(eq(phone), eq(savedOtp.getOtpCode()), eq("template-123"));
    assertEquals(savedOtp.getOtpCode(), otpCode);
  }

  @Test
  void generateAndSendOtp_WhenSendNotificationThrowsException_ShouldStillReturnOtp() throws Exception {
    String phone = "0123456789";
    String purpose = "REGISTER";
    doThrow(new RuntimeException("ZNS error")).when(znsNotificationService)
        .sendOtpNotification(anyString(), anyString(), anyString());

    String otpCode = otpServiceImpl.generateAndSendOtp(phone, purpose);

    assertNotNull(otpCode);
    verify(otpRepository).save(any(OtpVerification.class));
    verify(znsNotificationService).sendOtpNotification(anyString(), anyString(), anyString());
  }

  @Test
  void verifyOtp_WhenOtpValidAndPurposeMatches_ShouldMarkVerifiedAndReturnTrue() {
    String phone = "0123456789";
    String otpCode = "123456";
    String purpose = "LOGIN";
    OtpVerification otp = OtpVerification.builder()
        .phone(phone)
        .otpCode(otpCode)
        .expiresAt(LocalDateTime.now().plusMinutes(5))
        .isVerified(false)
        .purpose(purpose)
        .build();

    when(otpRepository.findByPhoneAndOtpCodeAndIsVerifiedFalseAndExpiresAtAfter(
        eq(phone), eq(otpCode), any(LocalDateTime.class)))
        .thenReturn(Optional.of(otp));

    boolean result = otpServiceImpl.verifyOtp(phone, otpCode, purpose);

    assertTrue(result);
    assertTrue(otp.getIsVerified());
    verify(otpRepository).save(otp);
    verify(otpRepository).markAsVerified(phone, purpose);
  }

  @Test
  void verifyOtp_WhenOtpNotFound_ShouldReturnFalse() {
    when(otpRepository.findByPhoneAndOtpCodeAndIsVerifiedFalseAndExpiresAtAfter(
        anyString(), anyString(), any(LocalDateTime.class)))
        .thenReturn(Optional.empty());

    boolean result = otpServiceImpl.verifyOtp("0123456789", "654321", "LOGIN");

    assertFalse(result);
    verify(otpRepository, never()).save(any());
    verify(otpRepository, never()).markAsVerified(anyString(), anyString());
  }

  @Test
  void verifyOtp_WhenPurposeMismatch_ShouldReturnFalse() {
    String phone = "0123456789";
    String otpCode = "123456";
    OtpVerification otp = OtpVerification.builder()
        .phone(phone)
        .otpCode(otpCode)
        .expiresAt(LocalDateTime.now().plusMinutes(5))
        .isVerified(false)
        .purpose("REGISTER")
        .build();

    when(otpRepository.findByPhoneAndOtpCodeAndIsVerifiedFalseAndExpiresAtAfter(
        eq(phone), eq(otpCode), any(LocalDateTime.class)))
        .thenReturn(Optional.of(otp));

    boolean result = otpServiceImpl.verifyOtp(phone, otpCode, "LOGIN");

    assertFalse(result);
    assertFalse(otp.getIsVerified());
    verify(otpRepository, never()).save(any());
    verify(otpRepository, never()).markAsVerified(anyString(), anyString());
  }
}
