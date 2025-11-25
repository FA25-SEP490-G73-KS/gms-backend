package fpt.edu.vn.gms.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class PaymentNotFoundException extends ResponseStatusException {

  public PaymentNotFoundException() {
    super(HttpStatus.NOT_FOUND, "Không tìm thấy phiếu thanh toán.");
  }

}