package fpt.edu.vn.gms.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class ServiceTicketNotFoundException extends ResponseStatusException {

  public ServiceTicketNotFoundException() {
    super(HttpStatus.NOT_FOUND, "Không tìm thấy phiếu dịch vụ.");
  }

}