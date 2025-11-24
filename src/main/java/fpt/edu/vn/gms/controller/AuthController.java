package fpt.edu.vn.gms.controller;

import fpt.edu.vn.gms.common.annotations.CurrentUser;
import fpt.edu.vn.gms.common.annotations.Public;
import fpt.edu.vn.gms.dto.request.ChangePasswordRequest;
import fpt.edu.vn.gms.dto.request.LoginRequestDto;
import fpt.edu.vn.gms.dto.request.RefreshRequestDto;
import fpt.edu.vn.gms.dto.request.ResetPasswordRequestDto;
import fpt.edu.vn.gms.dto.response.AccountResponseDto;
import fpt.edu.vn.gms.dto.response.ApiResponse;
import fpt.edu.vn.gms.dto.response.AuthTokenDto;
import fpt.edu.vn.gms.dto.response.ResetPasswordResponseDto;
import fpt.edu.vn.gms.entity.Employee;
import fpt.edu.vn.gms.service.auth.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import static fpt.edu.vn.gms.utils.AppRoutes.AUTH_PREFIX;

@Tag(name = "auth", description = "Xác thực & phân quyền")
@CrossOrigin(origins = "${fe-local-host}")
@RestController
@RequestMapping(path = AUTH_PREFIX, produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthController {

    AuthService authService;

    @Public
    @Operation(summary = "Đăng nhập", responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Đăng nhập"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Số điện thoại hoặc mật khẩu không đúng", content = @Content(schema = @Schema(hidden = true))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(schema = @Schema(hidden = true)))
    })
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthTokenDto>> login(
            @RequestBody LoginRequestDto request) {
        return ResponseEntity.ok(ApiResponse.success("Đăng nhập thành công", authService.login(request)));
    }

    @Public
    @Operation(summary = "Làm mới token", description = "Sử dụng refresh token để lấy access token mới", responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Làm mới token thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Refresh token không hợp lệ hoặc đã hết hạn", content = @Content(schema = @Schema(hidden = true))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(schema = @Schema(hidden = true)))
    })
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthTokenDto>> refresh(
            @RequestBody @Valid RefreshRequestDto request) {
        return ResponseEntity.ok(ApiResponse.success("Refresh token thành công", authService.refresh(request)));
    }

    @Operation(summary = "Đổi mật khẩu", description = "Người dùng đã đăng nhập thay đổi mật khẩu", responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Thay đổi mật khẩu thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Mật khẩu hiện tại không đúng", content = @Content(schema = @Schema(hidden = true))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy người dùng", content = @Content(schema = @Schema(hidden = true))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(schema = @Schema(hidden = true)))
    })
    @PutMapping("/change-password")
    public ResponseEntity<ApiResponse<AccountResponseDto>> changePassword(
            @RequestBody ChangePasswordRequest req,
            @CurrentUser Employee employee) {

        // Lấy phone từ token
        String phoneNumber = employee.getPhone();
        AccountResponseDto responseDTO = authService.changePassword(phoneNumber, req);
        return ResponseEntity.status(200)
                .body(ApiResponse.success("Thay đổi mật khẩu thành công", responseDTO));

    }

    @Public
    @Operation(summary = "Quên mật khẩu", description = "Yêu cầu mật khẩu mới khi quên mật khẩu", responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Yêu cầu thành công, mật khẩu mới đã được gửi qua SMS"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy người dùng với số điện thoại đã cho", content = @Content(schema = @Schema(hidden = true))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(schema = @Schema(hidden = true)))
    })
    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<ResetPasswordResponseDto>> resetPassword(
            @RequestBody ResetPasswordRequestDto req) {

        ResetPasswordResponseDto res = authService.resetPassword(req);
        return ResponseEntity.status(200)
                .body(ApiResponse.success("Làm mới mật khẩu thành công", res));
    }

    @Operation(summary = "Đăng xuất", description = "Đăng xuất khỏi hệ thống và vô hiệu hóa token", responses = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "Đăng xuất thành công", content = @Content(schema = @Schema(hidden = true))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Người dùng chưa đăng nhập", content = @Content(schema = @Schema(hidden = true))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(schema = @Schema(hidden = true)))
    })
    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(@CurrentUser Employee employee) {
        authService.logout(employee);
    }

}
