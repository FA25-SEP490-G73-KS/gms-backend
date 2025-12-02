package fpt.edu.vn.gms.controller;

import fpt.edu.vn.gms.common.annotations.Public;
import fpt.edu.vn.gms.dto.request.OtpRequestDto;
import fpt.edu.vn.gms.dto.request.OtpVerifyRequestDto;
import fpt.edu.vn.gms.dto.response.ApiResponse;
import fpt.edu.vn.gms.dto.response.CustomerDetailResponseDto;
import fpt.edu.vn.gms.service.CustomerService;
import fpt.edu.vn.gms.service.OtpService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static fpt.edu.vn.gms.utils.AppRoutes.OTP_PREFIX;

@Tag(name = "otp", description = "Quản lý mã OTP - Tạo và xác thực OTP qua ZNS")
@CrossOrigin(origins = "${fe-local-host}")
@RestController
@RequestMapping(path = OTP_PREFIX, produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class OtpController {

    OtpService otpService;
    CustomerService customerService;

    @Public
    @PostMapping("/send")
    @Operation(summary = "Gửi OTP qua ZNS", description = "Tạo và gửi mã OTP đến số điện thoại của khách hàng qua Zalo Notification Service")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Gửi OTP thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Không thể gửi OTP", content = @Content(schema = @Schema(hidden = true))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Lỗi máy chủ nội bộ", content = @Content(schema = @Schema(hidden = true)))
    })
    public ResponseEntity<ApiResponse<String>> sendOtp(@RequestBody OtpRequestDto request) {
        try {
            otpService.generateAndSendOtp(request.getPhone(), request.getPurpose());
            return ResponseEntity.ok(ApiResponse.success(
                    "OTP đã được gửi đến số điện thoại của bạn qua Zalo",
                    "OTP sent successfully" // In production, don't return actual OTP
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, "Không thể gửi OTP: " + e.getMessage()));
        }
    }

    @Public
    @PostMapping("/verify")
    @Operation(summary = "Xác thực OTP", description = "Xác thực mã OTP do khách hàng nhập")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "OTP hợp lệ"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "OTP không hợp lệ hoặc đã hết hạn", content = @Content(schema = @Schema(hidden = true))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Lỗi máy chủ nội bộ", content = @Content(schema = @Schema(hidden = true)))
    })
    public ResponseEntity<ApiResponse<Boolean>> verifyOtp(@RequestBody OtpVerifyRequestDto request) {
        boolean isValid = otpService.verifyOtp(
                request.getPhone(),
                request.getOtpCode(),
                request.getPurpose());

        if (isValid) {
            return ResponseEntity.ok(ApiResponse.success("OTP xác thực thành công", true));
        } else {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, "OTP không hợp lệ hoặc đã hết hạn"));
        }
    }

    @Public
    @PostMapping("/verify/customer")
    public ResponseEntity<ApiResponse<CustomerDetailResponseDto>> verifyOtpForCustomer(
            @RequestBody OtpVerifyRequestDto req
    ) {
        boolean ok = otpService.verifyOtp(req.getPhone(), req.getOtpCode(), req.getPurpose());

        // Lấy thông tin khách hàng
        CustomerDetailResponseDto dto = customerService.getByPhone(req.getPhone());

        return ResponseEntity.ok(
                ApiResponse.success("OTP hợp lệ", dto)
        );
    }

}
