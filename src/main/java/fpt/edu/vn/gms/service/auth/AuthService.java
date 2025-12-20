package fpt.edu.vn.gms.service.auth;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import fpt.edu.vn.gms.dto.request.ChangePasswordRequest;
import fpt.edu.vn.gms.dto.request.LoginRequestDto;
import fpt.edu.vn.gms.dto.request.RefreshRequestDto;
import fpt.edu.vn.gms.dto.request.ResetPasswordRequestDto;
import fpt.edu.vn.gms.dto.response.AccountResponseDto;
import fpt.edu.vn.gms.dto.response.AuthTokenDto;
import fpt.edu.vn.gms.dto.response.ResetPasswordResponseDto;
import fpt.edu.vn.gms.entity.Account;
import fpt.edu.vn.gms.entity.Employee;
import fpt.edu.vn.gms.exception.EmployeeNotFoundException;
import fpt.edu.vn.gms.exception.InvalidCredentialsException;
import fpt.edu.vn.gms.exception.TokenInvalidatedException;
import fpt.edu.vn.gms.mapper.AccountMapper;
import fpt.edu.vn.gms.repository.AccountRepository;
import fpt.edu.vn.gms.repository.EmployeeRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthService {

  AccountRepository accountRepository;
  EmployeeRepository employeeRepository;
  JwtService jwtService;
  PasswordEncoder passwordEncoder;
  AccountMapper accountMapper;
  RedisService redisService;

  public AuthTokenDto login(LoginRequestDto request) {
    String phone = request.getPhone();
    String password = request.getPassword();

    Account account = accountRepository.findByPhone(phone).orElseThrow(InvalidCredentialsException::new);

    if (!passwordEncoder.matches(password, account.getPassword())) {
      throw new InvalidCredentialsException();
    }

    Employee employee = employeeRepository.findByAccount(account).orElseThrow(InvalidCredentialsException::new);

    boolean rememberMe = Boolean.TRUE.equals(request.getRememberMe());
    return getTokens(employee, rememberMe);
  }

  public AuthTokenDto refresh(RefreshRequestDto request) {
    Jws<Claims> decodedRefreshToken = jwtService.verifyRefreshToken(request.getRefreshToken());
    Long employeeId = Long.parseLong(decodedRefreshToken.getBody().getSubject());
    Date tokenIssuedAt = decodedRefreshToken.getBody().getIssuedAt();

    if (jwtService.isTokenInvalidated(employeeId, tokenIssuedAt)) {
      throw new TokenInvalidatedException();
    }

    Employee employee = employeeRepository.findById(employeeId).orElseThrow(EmployeeNotFoundException::new);

    // Lấy thông tin rememberMe từ refresh token cũ (nếu có)
    Boolean rememberMeClaim = decodedRefreshToken.getBody().get("rememberMe", Boolean.class);
    boolean rememberMe = Boolean.TRUE.equals(rememberMeClaim);

    invalidateTokens(employeeId);
    return getTokens(employee, rememberMe);
  }

  @Transactional
  public AccountResponseDto changePassword(String phoneNumber, ChangePasswordRequest req) {

    Account acc = accountRepository.findByPhone(phoneNumber)
        .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng!"));

    if (!passwordEncoder.matches(req.getCurrentPassword(), acc.getPassword())) {
      throw new RuntimeException("Mật khẩu hiện tại không đúng!");
    }

    if (req.getNewPassword() == null || req.getNewPassword().length() < 8) {
      throw new RuntimeException("Mật khẩu mới phải có ít nhất 8 ký tự!");
    }

    if (passwordEncoder.matches(req.getNewPassword(), acc.getPassword())) {
      throw new RuntimeException("Mật khẩu mới phải khác mật khẩu hiện tại!");
    }

    if (!req.getNewPassword().equals(req.getConfirmPassword())) {
      throw new RuntimeException("Xác nhận mật khẩu không khớp!");
    }

    acc.setPassword(passwordEncoder.encode(req.getNewPassword()));
    accountRepository.save(acc);

    // Invalidate tokens nếu có employee và Redis available
    // Tìm employee trực tiếp từ repository để tránh lazy loading exception
    try {
      Employee employee = employeeRepository.findByAccount(acc).orElse(null);
      if (employee != null && employee.getEmployeeId() != null) {
        try {
          invalidateTokens(employee.getEmployeeId());
        } catch (Exception e) {
          log.warn("Không thể invalidate tokens trong Redis cho user {}: {}", phoneNumber, e.getMessage());
          // Không throw exception để không làm fail việc đổi mật khẩu
          // Mật khẩu đã được đổi thành công, chỉ là không thể invalidate token cũ
        }
      } else {
        log.warn("Account {} không có employee để invalidate tokens", phoneNumber);
      }
    } catch (Exception e) {
      log.warn("Không thể lấy employee cho account {}: {}", phoneNumber, e.getMessage());
      // Không throw exception để không làm fail việc đổi mật khẩu
    }

    log.info("User {} đã đổi mật khẩu thành công!", acc.getPhone());

    return accountMapper.toDTO(acc);
  }

  @Transactional
  public ResetPasswordResponseDto resetPassword(ResetPasswordRequestDto dto) {

    String phone = dto.getPhone() != null ? dto.getPhone().trim() : null;
    if (phone == null || phone.isEmpty()) {
      throw new RuntimeException("Số điện thoại không được để trống");
    }

    log.debug("Đang tìm account với số điện thoại: [{}]", phone);

    // Thử tìm với exact match trước
    Account account = accountRepository.findByPhone(phone).orElse(null);

    if (account == null) {
      account = accountRepository.findByPhoneTrimmed(phone).orElse(null);
    }

    if (account == null) {
      log.error("Không tìm thấy account với số điện thoại: [{}]", phone);
      throw new RuntimeException("Số điện thoại không tồn tại trong hệ thống");
    }

    if (!account.isActive()) {
      log.warn("Account với số điện thoại {} không active", phone);
      throw new RuntimeException("Tài khoản đã bị vô hiệu hóa");
    }

    if (dto.getNewPassword() == null || dto.getNewPassword().length() < 8) {
      throw new RuntimeException("Mật khẩu phải có ít nhất 8 ký tự");
    }

    if (!dto.getNewPassword().equals(dto.getConfirmPassword())) {
      throw new RuntimeException("Mật khẩu xác nhận không khớp");
    }

    account.setPassword(passwordEncoder.encode(dto.getNewPassword()));
    accountRepository.save(account);

    // Invalidate tokens nếu có employee và Redis available
    // Tìm employee trực tiếp từ repository để tránh lazy loading exception
    try {
      Employee employee = employeeRepository.findByAccount(account).orElse(null);
      if (employee != null && employee.getEmployeeId() != null) {
        try {
          invalidateTokens(employee.getEmployeeId());
        } catch (Exception e) {
          log.warn("Không thể invalidate tokens trong Redis cho user {}: {}", dto.getPhone(), e.getMessage());
          // Không throw exception để không làm fail việc đổi mật khẩu
          // Mật khẩu đã được đổi thành công, chỉ là không thể invalidate token cũ
        }
      } else {
        log.warn("Account {} không có employee để invalidate tokens", dto.getPhone());
      }
    } catch (Exception e) {
      log.warn("Không thể lấy employee cho account {}: {}", dto.getPhone(), e.getMessage());
      // Không throw exception để không làm fail việc đổi mật khẩu
    }

    log.info("User {} đã đổi mật khẩu thành công", account.getPhone());

    return ResetPasswordResponseDto.builder()
        .phone(account.getPhone())
        .message("Đổi mật khẩu thành công. Vui lòng đăng nhập lại.")
        .build();
  }

  public void logout(Employee employee) {
    try {
      invalidateTokens(employee.getEmployeeId());
    } catch (Exception e) {
      log.warn("Không thể invalidate tokens trong Redis khi logout: {}", e.getMessage());
      // Không throw exception để không làm fail việc logout
    }
  }

  private AuthTokenDto getTokens(Employee employee) {
    return getTokens(employee, false);
  }

  private AuthTokenDto getTokens(Employee employee, boolean rememberMe) {
    String accessToken = jwtService.generateAccessToken(employee);
    String refreshToken = jwtService.generateRefreshToken(employee, rememberMe);

    return new AuthTokenDto(accessToken, refreshToken);
  }

  private void invalidateTokens(Long employeeId) {
    redisService.set(
        "user:%s:tokens:invalidated_before".formatted(employeeId),
        Instant.now().truncatedTo(ChronoUnit.SECONDS));
  }

}