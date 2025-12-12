package fpt.edu.vn.gms.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class TransactionNotFoundException extends ResponseStatusException {

  public TransactionNotFoundException() {
    super(HttpStatus.NOT_FOUND, "Không tìm thấy giao dịch");
  }

}
