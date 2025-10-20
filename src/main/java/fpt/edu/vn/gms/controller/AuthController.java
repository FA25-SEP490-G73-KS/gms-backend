package fpt.edu.vn.gms.controller;

import fpt.edu.vn.gms.dto.*;
import fpt.edu.vn.gms.service.impl.AuthServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthServiceImpl authService;

    @PostMapping("/login")
    public LoginResponseDto login(@RequestBody LoginRequestDto dto) {
        return authService.login(dto);
    }

    @PostMapping("/forgot-password")
    public String forgotPassword(@RequestBody ForgotPasswordDto dto) {
        authService.forgotPassword(dto);
        return "OTP sent successfully!";
    }
}
