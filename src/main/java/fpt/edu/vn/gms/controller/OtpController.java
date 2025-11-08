package fpt.edu.vn.gms.controller;

import fpt.edu.vn.gms.dto.request.OtpRequestDto;
import fpt.edu.vn.gms.dto.request.OtpVerifyRequestDto;
import fpt.edu.vn.gms.dto.response.ApiResponse;
import fpt.edu.vn.gms.service.OtpService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/otp")
@RequiredArgsConstructor
@Tag(name = "OTP Management", description = "APIs for OTP generation and verification via ZNS")
public class OtpController {

    private final OtpService otpService;

    @PostMapping("/send")
    @Operation(
            summary = "Send OTP via ZNS",
            description = "Generates and sends OTP code to customer's phone number via Zalo Notification Service"
    )
    public ResponseEntity<ApiResponse<String>> sendOtp(@RequestBody OtpRequestDto request) {
        try {
            String otpCode = otpService.generateAndSendOtp(request.getPhone(), request.getPurpose());
            return ResponseEntity.ok(ApiResponse.success(
                    "OTP đã được gửi đến số điện thoại của bạn qua Zalo", 
                    "OTP sent successfully" // In production, don't return actual OTP
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, "Không thể gửi OTP: " + e.getMessage()));
        }
    }

    @PostMapping("/verify")
    @Operation(
            summary = "Verify OTP",
            description = "Verifies the OTP code entered by customer"
    )
    public ResponseEntity<ApiResponse<Boolean>> verifyOtp(@RequestBody OtpVerifyRequestDto request) {
        boolean isValid = otpService.verifyOtp(
                request.getPhone(), 
                request.getOtpCode(), 
                request.getPurpose()
        );

        if (isValid) {
            return ResponseEntity.ok(ApiResponse.success("OTP xác thực thành công", true));
        } else {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, "OTP không hợp lệ hoặc đã hết hạn"));
        }
    }
}

