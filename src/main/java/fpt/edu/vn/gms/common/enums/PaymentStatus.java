package fpt.edu.vn.gms.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum PaymentStatus {
  PAID_IN_FULL("PAID_IN_FULL"),
  UNDERPAID("UNDERPAID"),
  PENDING("PENDING");

  private final String value;
}