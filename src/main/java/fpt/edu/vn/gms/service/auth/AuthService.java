package fpt.edu.vn.gms.service.auth;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

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
    return getTokens(employee);
  }

  public AuthTokenDto refresh(RefreshRequestDto request) {
    Jws<Claims> decodedRefreshToken = jwtService.verifyRefreshToken(request.getRefreshToken());
    Long employeeId = Long.parseLong(decodedRefreshToken.getBody().getSubject());
    Date tokenIssuedAt = decodedRefreshToken.getBody().getIssuedAt();

    if (jwtService.isTokenInvalidated(employeeId, tokenIssuedAt)) {
      throw new TokenInvalidatedException();
    }

    Employee employee = employeeRepository.findById(employeeId).orElseThrow(EmployeeNotFoundException::new);

    invalidateTokens(employeeId);
    return getTokens(employee);
  }

  public AccountResponseDto changePassword(String phoneNumber, ChangePasswordRequest req) {

    Account acc = accountRepository.findByPhone(phoneNumber)
        .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng!"));

    System.out.println(req.getNewPassword());
    System.out.println(acc.getPassword());
    if (!passwordEncoder.matches(req.getCurrentPassword(), acc.getPassword())) {
      throw new RuntimeException("Mật khẩu hiện tại không đúng!");
    }

    acc.setPassword(passwordEncoder.encode(req.getNewPassword()));
    accountRepository.save(acc);
    return accountMapper.toDTO(acc);
  }

  public ResetPasswordResponseDto resetPassword(ResetPasswordRequestDto resetPasswordRequestDTO) {

    Account account = accountRepository.findByPhone(resetPasswordRequestDTO.getPhone())
        .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

    String newPassword = generateRandomPassword();

    account.setPassword(passwordEncoder.encode(newPassword));
    accountRepository.save(account);

    log.info("Gửi SMS đến " + account.getPhone() + " Mật khẩu mới là: " + newPassword);

    return ResetPasswordResponseDto.builder()
        .phone(account.getPhone())
        .message("Mật khẩu mới đã được gửi qua SMS")
        .build();
  }

  public void logout(Employee employee) {
    invalidateTokens(employee.getEmployeeId());
  }

  private AuthTokenDto getTokens(Employee employee) {
    String accessToken = jwtService.generateAccessToken(employee);
    String refreshToken = jwtService.generateRefreshToken(employee);

    return new AuthTokenDto(accessToken, refreshToken);
  }

  private String generateRandomPassword() {
    return UUID.randomUUID().toString().substring(0, 8); // random 8 ký tự
  }

  private void invalidateTokens(Long employeeId) {
    redisService.set(
        "user:%s:tokens:invalidated_before".formatted(employeeId),
        Instant.now().truncatedTo(ChronoUnit.SECONDS));
  }

}