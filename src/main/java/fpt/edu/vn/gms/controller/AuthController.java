package fpt.edu.vn.gms.controller;

import com.google.firebase.auth.FirebaseAuthException;
import fpt.edu.vn.gms.dto.request.LoginRequestDTO;
import fpt.edu.vn.gms.dto.response.LoginResponseDTO;
import fpt.edu.vn.gms.dto.request.ResetPasswordRequestDTO;
import fpt.edu.vn.gms.dto.response.ResetPasswordResponseDTO;
import fpt.edu.vn.gms.security.JwtUtils;
import fpt.edu.vn.gms.service.AccountDetailsService;
import fpt.edu.vn.gms.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "${fe-local-host}")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication Management", description = "APIs for user authentication and password reset")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final AccountDetailsService accountDetailsService;
    private final AccountService accountService;

    // ========== LOGIN ==========
    @PostMapping("/login")
    @Operation(
            summary = "User login",
            description = "Authenticates user by phone and password, then returns a JWT token for authorized access."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful, JWT token returned",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = LoginResponseDTO.class),
                            examples = @ExampleObject(value = "{\"token\": \"eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...\"}")
                    )),
            @ApiResponse(responseCode = "401", description = "Invalid phone number or password",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"error\": \"Invalid credentials\"}")
                    ))
    })
    public ResponseEntity<LoginResponseDTO> login(
            @RequestBody(
                    description = "User login credentials",
                    required = true,
                    content = @Content(schema = @Schema(implementation = LoginRequestDTO.class),
                            examples = @ExampleObject(
                                    name = "Login Example",
                                    value = "{\"phone\": \"0987654321\", \"password\": \"123456\"}"
                            )
                    )
            )
            @org.springframework.web.bind.annotation.RequestBody LoginRequestDTO request
    ) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getPhone(), request.getPassword())
        );

        UserDetails userDetails = accountDetailsService.loadUserByUsername(request.getPhone());
        String token = jwtUtils.generateToken(userDetails);

        return ResponseEntity.ok(new LoginResponseDTO(token));
    }

    // ========== RESET PASSWORD ==========
    @PostMapping("/reset-password")
    @Operation(
            summary = "Reset password via OTP",
            description = "Resets user password after verifying OTP token received via Firebase Authentication."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password reset successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ResetPasswordResponseDTO.class),
                            examples = @ExampleObject(
                                    name = "Success Example",
                                    value = "{\"uid\": \"abcd1234\", \"email\": \"user@example.com\", \"phone\": \"0987654321\", \"message\": \"Password updated successfully\"}"
                            )
                    )),
            @ApiResponse(responseCode = "401", description = "Invalid OTP or token",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"message\": \"Invalid OTP or token\"}")
                    )),
            @ApiResponse(responseCode = "400", description = "Invalid request data",
                    content = @Content(mediaType = "application/json",
                            examples = @ExampleObject(value = "{\"message\": \"Phone number not registered\"}")
                    ))
    })
    public ResponseEntity<ResetPasswordResponseDTO> resetPassword(
            @RequestBody(
                    description = "Password reset request containing OTP verification token and new password",
                    required = true,
                    content = @Content(schema = @Schema(implementation = ResetPasswordRequestDTO.class),
                            examples = @ExampleObject(
                                    name = "Reset Password Example",
                                    value = "{\"idToken\": \"firebase-otp-idtoken\", \"newPassword\": \"newpass123\"}"
                            )
                    )
            )
            @org.springframework.web.bind.annotation.RequestBody ResetPasswordRequestDTO requestDTO
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
}
