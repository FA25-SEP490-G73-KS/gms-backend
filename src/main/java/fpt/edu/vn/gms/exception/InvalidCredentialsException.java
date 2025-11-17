package fpt.edu.vn.gms.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class InvalidCredentialsException extends ResponseStatusException {

  public InvalidCredentialsException() {
    super(HttpStatus.UNAUTHORIZED, "Số điện thoại hoặc mật khẩu không đúng");
  }

  public InvalidCredentialsException(String message) {
    super(HttpStatus.UNAUTHORIZED, message);
  }
}
