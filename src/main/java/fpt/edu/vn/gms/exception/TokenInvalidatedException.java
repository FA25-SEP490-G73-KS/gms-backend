package fpt.edu.vn.gms.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class TokenInvalidatedException extends ResponseStatusException {

  public TokenInvalidatedException() {
    super(HttpStatus.UNAUTHORIZED, "Token is invalidated");
  }
}
