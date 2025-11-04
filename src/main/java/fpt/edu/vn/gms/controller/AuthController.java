package fpt.edu.vn.gms.controller;

import com.google.firebase.auth.FirebaseAuthException;
import fpt.edu.vn.gms.dto.request.ChangePasswordRequest;
import fpt.edu.vn.gms.dto.request.LoginRequestDto;
import fpt.edu.vn.gms.dto.response.AccountResponseDto;
import fpt.edu.vn.gms.dto.response.ApiResponse;
import fpt.edu.vn.gms.dto.response.LoginResponseDto;
import fpt.edu.vn.gms.dto.request.ResetPasswordRequestDTO;
import fpt.edu.vn.gms.dto.response.ResetPasswordResponseDTO;
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

    // ========== RESET PASSWORD ==========
    @PostMapping("/reset-password")
    public ResponseEntity<ResetPasswordResponseDTO> resetPassword(
          @RequestBody ResetPasswordRequestDTO requestDTO
    ) {
        try {
            ResetPasswordResponseDTO responseDTO = accountService.resetPassword(requestDTO);
            return ResponseEntity.ok(responseDTO);
        } catch (FirebaseAuthException e) {
            return ResponseEntity.status(401)
                    .body(new ResetPasswordResponseDTO(null, null, null, "Invalid OTP or token"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest()
                    .body(new ResetPasswordResponseDTO(null, null, null, e.getMessage()));
        }
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
}
