package fpt.edu.vn.gms.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum DebtStatus {

    OUTSTANDING("OUTSTANDING"),
    PAID_IN_FULL("PAID_IN_FULL");

    private final String value;
}
