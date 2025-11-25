package fpt.edu.vn.gms.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TransactionMethod {
  CASH("CASH"),
  BANK_TRANSFER("BANK_TRANSFER");

  private final String value;
}