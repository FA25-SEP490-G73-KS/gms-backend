package fpt.edu.vn.gms.service.impl;

import fpt.edu.vn.gms.dto.*;
import fpt.edu.vn.gms.entity.Account;
import fpt.edu.vn.gms.repository.AccountRepository;
import fpt.edu.vn.gms.utils.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl {

    private final AuthenticationManager authManager;
    private final AccountRepository accountRepository;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;
    private final SmsServiceViettelImpl smsService;

    public LoginResponseDto login(LoginRequestDto dto) {
        authManager.authenticate(new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword()));
        Account acc = accountRepository.findByUsername(dto.getUsername()).orElseThrow();
        String token = jwtUtils.generateToken(acc.getUsername());
        return new LoginResponseDto(token, acc.getUsername(), acc.getRole().getName());
    }

    public void forgotPassword(ForgotPasswordDto dto) {
        Account acc = accountRepository.findByPhone(dto.getPhone())
                .orElseThrow(() -> new RuntimeException("Account not found with phone: " + dto.getPhone()));

        String otp = String.valueOf((int) (Math.random() * 900000 + 100000));
        smsService.sendSms(dto.getPhone(), "Your GMS OTP is: " + otp);
        // Tạm in OTP ra log
        System.out.println("OTP for " + acc.getUsername() + " is " + otp);
    }
}
