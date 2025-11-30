package fpt.edu.vn.gms.common.enums;

import java.util.stream.Stream;
import fpt.edu.vn.gms.exception.ResourceNotFoundException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TransactionMethod {
  CASH("CASH"),
  BANK_TRANSFER("BANK_TRANSFER");

  private final String value;

  public static TransactionMethod fromValue(String value) {
    return Stream.of(TransactionMethod.values())
        .filter(type -> type.getValue().equals(value))
        .findFirst()
        .orElseThrow(() -> new ResourceNotFoundException(
            "TransactionMethod '%s' is not found".formatted(value)));
  }
}