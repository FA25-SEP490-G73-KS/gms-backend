package fpt.edu.vn.gms.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class EmployeeNotFoundException extends ResponseStatusException {

  public EmployeeNotFoundException() {
    super(HttpStatus.NOT_FOUND, "Không tìm thấy nhân viên!");
  }

}
