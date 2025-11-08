package fpt.edu.vn.gms.service;

public interface OtpService {
    /**
     * Generate and send OTP to customer via ZNS
     * @param phone Customer phone number
     * @param purpose Purpose of OTP (e.g., "APPOINTMENT")
     * @return OTP code (for testing purposes, in production should not return)
     */
    String generateAndSendOtp(String phone, String purpose);

    /**
     * Verify OTP code
     * @param phone Customer phone number
     * @param otpCode OTP code to verify
     * @param purpose Purpose of OTP
     * @return true if OTP is valid and not expired
     */
    boolean verifyOtp(String phone, String otpCode, String purpose);
}

