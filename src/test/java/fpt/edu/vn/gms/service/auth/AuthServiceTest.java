package fpt.edu.vn.gms.service.auth;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.Instant;
import java.util.Date;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.crypto.password.PasswordEncoder;

import fpt.edu.vn.gms.base.BaseServiceTest;
import fpt.edu.vn.gms.common.enums.Role;
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

public class AuthServiceTest extends BaseServiceTest {

  @Mock
  private AccountRepository accountRepository;

  @Mock
  private EmployeeRepository employeeRepository;

  @Mock
  private JwtService jwtService;

  @Mock
  private PasswordEncoder passwordEncoder;

  @Mock
  private AccountMapper accountMapper;

  @Mock
  private RedisService redisService;

  @InjectMocks
  private AuthService authService;

  @Test
  void login_WhenValidCredentials_ShouldReturnAuthTokenDto() {
    LoginRequestDto request = new LoginRequestDto();
    request.setPhone("0123456789");
    request.setPassword("123456");

    Account mockAccount = getMockAccount(Role.MANAGER);
    Employee mockEmployee = getMockEmployee(Role.MANAGER);

    when(accountRepository.findByPhone("0123456789")).thenReturn(Optional.of(mockAccount));
    when(passwordEncoder.matches("123456", mockAccount.getPassword())).thenReturn(true);
    when(employeeRepository.findByAccount(mockAccount)).thenReturn(Optional.of(mockEmployee));
    when(jwtService.generateAccessToken(mockEmployee)).thenReturn("access-token");
    when(jwtService.generateRefreshToken(mockEmployee)).thenReturn("refresh-token");

    AuthTokenDto result = authService.login(request);

    assertNotNull(result);
    assertEquals("access-token", result.getAccessToken());
    assertEquals("refresh-token", result.getRefreshToken());
  }

  @Test
  void login_WhenAccountNotFound_ShouldThrowInvalidCredentialsException() {
    LoginRequestDto request = new LoginRequestDto();
    request.setPhone("notfound");
    request.setPassword("123456");

    when(accountRepository.findByPhone("notfound")).thenReturn(Optional.empty());

    assertThrows(InvalidCredentialsException.class, () -> authService.login(request));
  }

  @Test
  void login_WhenPasswordNotMatch_ShouldThrowInvalidCredentialsException() {
    LoginRequestDto request = new LoginRequestDto();
    request.setPhone("0123456789");
    request.setPassword("wrongpass");

    Account mockAccount = getMockAccount(Role.MANAGER);

    when(accountRepository.findByPhone("0123456789")).thenReturn(Optional.of(mockAccount));
    when(passwordEncoder.matches("wrongpass", mockAccount.getPassword())).thenReturn(false);

    assertThrows(InvalidCredentialsException.class, () -> authService.login(request));
  }

  @Test
  void login_WhenEmployeeNotFound_ShouldThrowInvalidCredentialsException() {
    LoginRequestDto request = new LoginRequestDto();
    request.setPhone("0123456789");
    request.setPassword("123456");

    Account mockAccount = getMockAccount(Role.MANAGER);

    when(accountRepository.findByPhone("0123456789")).thenReturn(Optional.of(mockAccount));
    when(passwordEncoder.matches("123456", mockAccount.getPassword())).thenReturn(true);
    when(employeeRepository.findByAccount(mockAccount)).thenReturn(Optional.empty());

    assertThrows(InvalidCredentialsException.class, () -> authService.login(request));
  }

  @Test
  void refresh_WhenValidRefreshToken_ShouldReturnAuthTokenDto() {
    RefreshRequestDto request = new RefreshRequestDto();
    request.setRefreshToken("refresh-token");

    Claims claims = mock(Claims.class);
    when(claims.getSubject()).thenReturn("1");
    Date issuedAt = new Date();
    when(claims.getIssuedAt()).thenReturn(issuedAt);

    Jws<Claims> jws = mock(Jws.class);
    when(jws.getBody()).thenReturn(claims);

    Employee mockEmployee = getMockEmployee(Role.MANAGER);

    when(jwtService.verifyRefreshToken("refresh-token")).thenReturn(jws);
    when(jwtService.isTokenInvalidated(1L, issuedAt)).thenReturn(false);
    when(employeeRepository.findById(1L)).thenReturn(Optional.of(mockEmployee));
    when(jwtService.generateAccessToken(mockEmployee)).thenReturn("access-token");
    when(jwtService.generateRefreshToken(mockEmployee)).thenReturn("refresh-token");

    AuthTokenDto result = authService.refresh(request);

    assertNotNull(result);
    assertEquals("access-token", result.getAccessToken());
    assertEquals("refresh-token", result.getRefreshToken());
    verify(redisService).set(eq("user:1:tokens:invalidated_before"), any(Instant.class));
  }

  @Test
  void refresh_WhenTokenInvalidated_ShouldThrowTokenInvalidatedException() {
    RefreshRequestDto request = new RefreshRequestDto();
    request.setRefreshToken("refresh-token");

    Claims claims = mock(Claims.class);
    when(claims.getSubject()).thenReturn("1");
    Date issuedAt = new Date();
    when(claims.getIssuedAt()).thenReturn(issuedAt);

    Jws<Claims> jws = mock(Jws.class);
    when(jws.getBody()).thenReturn(claims);

    when(jwtService.verifyRefreshToken("refresh-token")).thenReturn(jws);
    when(jwtService.isTokenInvalidated(1L, issuedAt)).thenReturn(true);

    assertThrows(TokenInvalidatedException.class, () -> authService.refresh(request));
  }

  @Test
  void refresh_WhenEmployeeNotFound_ShouldThrowEmployeeNotFoundException() {
    RefreshRequestDto request = new RefreshRequestDto();
    request.setRefreshToken("refresh-token");

    Claims claims = mock(Claims.class);
    when(claims.getSubject()).thenReturn("1");
    Date issuedAt = new Date();
    when(claims.getIssuedAt()).thenReturn(issuedAt);

    Jws<Claims> jws = mock(Jws.class);
    when(jws.getBody()).thenReturn(claims);

    when(jwtService.verifyRefreshToken("refresh-token")).thenReturn(jws);
    when(jwtService.isTokenInvalidated(1L, issuedAt)).thenReturn(false);
    when(employeeRepository.findById(1L)).thenReturn(Optional.empty());

    assertThrows(EmployeeNotFoundException.class, () -> authService.refresh(request));
  }

  @Test
  void changePassword_WhenCurrentPasswordCorrect_ShouldUpdatePasswordAndReturnDto() {
    String phone = "0123456789";
    ChangePasswordRequest req = ChangePasswordRequest.builder()
        .currentPassword("oldpass")
        .newPassword("newpass")
        .build();

    Account mockAccount = getMockAccount(Role.MANAGER);

    when(accountRepository.findByPhone(phone)).thenReturn(Optional.of(mockAccount));
    when(passwordEncoder.matches("oldpass", mockAccount.getPassword())).thenReturn(true);
    when(passwordEncoder.encode("newpass")).thenReturn("encodedNewPass");
    when(accountRepository.save(any(Account.class))).thenReturn(mockAccount);

    AccountResponseDto dto = new AccountResponseDto();
    when(accountMapper.toDTO(mockAccount)).thenReturn(dto);

    AccountResponseDto result = authService.changePassword(phone, req);

    assertEquals(dto, result);
    verify(accountRepository).save(mockAccount);
    assertEquals("encodedNewPass", mockAccount.getPassword());
  }

  @Test
  void changePassword_WhenAccountNotFound_ShouldThrowRuntimeException() {
    String phone = "notfound";
    ChangePasswordRequest req = ChangePasswordRequest.builder()
        .currentPassword("oldpass")
        .newPassword("newpass")
        .build();

    when(accountRepository.findByPhone(phone)).thenReturn(Optional.empty());

    RuntimeException ex = assertThrows(RuntimeException.class, () -> authService.changePassword(phone, req));
    assertTrue(ex.getMessage().contains("Không tìm thấy người dùng"));
  }

  @Test
  void changePassword_WhenCurrentPasswordIncorrect_ShouldThrowRuntimeException() {
    String phone = "0123456789";
    ChangePasswordRequest req = ChangePasswordRequest.builder()
        .currentPassword("wrongpass")
        .newPassword("newpass")
        .build();

    Account mockAccount = getMockAccount(Role.MANAGER);

    when(accountRepository.findByPhone(phone)).thenReturn(Optional.of(mockAccount));
    when(passwordEncoder.matches("wrongpass", mockAccount.getPassword())).thenReturn(false);

    RuntimeException ex = assertThrows(RuntimeException.class, () -> authService.changePassword(phone, req));
    assertTrue(ex.getMessage().contains("Mật khẩu hiện tại không đúng"));
  }

  @Test
  void resetPassword_WhenAccountExists_ShouldResetPasswordAndReturnResponse() {
    ResetPasswordRequestDto req = ResetPasswordRequestDto.builder().phone("0123456789").build();

    Account mockAccount = getMockAccount(Role.MANAGER);

    when(accountRepository.findByPhone("0123456789")).thenReturn(Optional.of(mockAccount));
    when(passwordEncoder.encode(anyString())).thenReturn("encodedNewPass");
    when(accountRepository.save(any(Account.class))).thenReturn(mockAccount);

    ResetPasswordResponseDto result = authService.resetPassword(req);

    assertNotNull(result);
    assertEquals("0123456789", result.getPhone());
    assertEquals("Mật khẩu mới đã được gửi qua SMS", result.getMessage());
    verify(accountRepository).save(mockAccount);
    assertEquals("encodedNewPass", mockAccount.getPassword());
  }

  @Test
  void resetPassword_WhenAccountNotFound_ShouldThrowRuntimeException() {
    ResetPasswordRequestDto req = ResetPasswordRequestDto.builder().phone("notfound").build();

    when(accountRepository.findByPhone("notfound")).thenReturn(Optional.empty());

    RuntimeException ex = assertThrows(RuntimeException.class, () -> authService.resetPassword(req));
    assertTrue(ex.getMessage().contains("Không tìm thấy người dùng"));
  }

  @Test
  void logout_WhenCalled_ShouldInvalidateTokens() {
    Employee employee = getMockEmployee(Role.MANAGER);

    authService.logout(employee);

    verify(redisService).set(eq("user:%s:tokens:invalidated_before".formatted(employee.getEmployeeId())),
        any(Instant.class));
  }
}
