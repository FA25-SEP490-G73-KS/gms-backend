package fpt.edu.vn.gms.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DiscountType {
  AMOUNT("AMOUNT"),
  PERCENTAGE("PERCENTAGE");

  private final String value;

}