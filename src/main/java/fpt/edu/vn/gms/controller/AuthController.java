package fpt.edu.vn.gms.controller;

import fpt.edu.vn.gms.dto.request.ChangePasswordRequest;
import fpt.edu.vn.gms.dto.request.LoginRequestDto;
import fpt.edu.vn.gms.dto.request.ResetPasswordRequestDto;
import fpt.edu.vn.gms.dto.response.AccountResponseDto;
import fpt.edu.vn.gms.dto.response.ApiResponse;
import fpt.edu.vn.gms.dto.response.LoginResponseDto;
import fpt.edu.vn.gms.dto.response.ResetPasswordResponseDto;
import fpt.edu.vn.gms.security.JwtUtils;
import fpt.edu.vn.gms.service.AccountDetailsService;
import fpt.edu.vn.gms.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "${fe-local-host}")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final AccountDetailsService accountDetailsService;
    private final AccountService accountService;

    // ========== LOGIN ==========
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(
            @RequestBody LoginRequestDto request
    ) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getPhone(), request.getPassword())
        );

        UserDetails userDetails = accountDetailsService.loadUserByUsername(request.getPhone());
        String token = jwtUtils.generateToken(userDetails);

        return ResponseEntity.ok(new LoginResponseDto(token));
    }

    @PutMapping("/change-password")
    public ResponseEntity<ApiResponse<AccountResponseDto>> changePassword(
            @RequestBody ChangePasswordRequest req,
            Authentication authentication
            ) {

        // Lấy phone từ token
        String phoneNumber = authentication.getName();
        AccountResponseDto responseDTO = accountService.changePassword(phoneNumber, req);
        return ResponseEntity.status(200)
                .body(ApiResponse.success("Thay đổi mật khẩu thành công", responseDTO));

    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<ResetPasswordResponseDto>> resetPassword(
            @RequestBody ResetPasswordRequestDto req
            ) {

            ResetPasswordResponseDto res = accountService.resetPassword(req);
            return ResponseEntity.status(200)
                    .body(ApiResponse.success("Làm mới mật khẩu thành công!!!", res));
    }


}
