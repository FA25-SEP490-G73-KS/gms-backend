package fpt.edu.vn.gms.common.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * CustomerLoyaltyLevel enum là loại cấp độ khách hàng
 */

@Getter
@RequiredArgsConstructor
public enum CustomerLoyaltyLevel {
    BRONZE("BRONZE"),
    SLIVER("SLIVER"),
    GOLD("GOLD");

    private final String value;
}
