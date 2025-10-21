package fpt.edu.vn.gms.controller;

import com.google.firebase.auth.FirebaseAuthException;
import fpt.edu.vn.gms.dto.LoginRequestDTO;
import fpt.edu.vn.gms.dto.LoginResponseDTO;
import fpt.edu.vn.gms.dto.ResetPasswordRequestDTO;
import fpt.edu.vn.gms.dto.ResetPasswordResponseDTO;
import fpt.edu.vn.gms.security.JwtUtils;
import fpt.edu.vn.gms.service.AccountDetailsService;
import fpt.edu.vn.gms.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final AccountDetailsService accountDetailsService;
    private final AccountService accountService;

    @PostMapping("/login")
    public LoginResponseDTO login(@RequestBody LoginRequestDTO request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getPhone(), request.getPassword())
        );

        UserDetails userDetails = accountDetailsService.loadUserByUsername(request.getPhone());
        String token = jwtUtils.generateToken(userDetails.getUsername());

        return new LoginResponseDTO(token);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ResetPasswordResponseDTO> resetPassword(
            @RequestBody ResetPasswordRequestDTO requestDTO) {
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
}