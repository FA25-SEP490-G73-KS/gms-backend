package fpt.edu.vn.gms.common.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum InvoiceStatus {
  PAID_IN_FULL("Thanh toán đủ"),
  UNDERPAID("Thanh toán thiếu"),
  PENDING("Chờ thanh toán");

  private final String value;

  @JsonValue
  public String getValue() {
    return this.value;
  }
}