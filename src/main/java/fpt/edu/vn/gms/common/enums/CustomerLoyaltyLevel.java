package fpt.edu.vn.gms.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CustomerLoyaltyLevel {
    BRONZE("BRONZE"),
    SLIVER("SLIVER"),
    GOLD("GOLD");

    private final String value;
}
