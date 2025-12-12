package fpt.edu.vn.gms.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class DebtNotFoundException extends ResponseStatusException {

  public DebtNotFoundException() {
    super(HttpStatus.NOT_FOUND, "Không tìm thấy công nợ");
  }

}