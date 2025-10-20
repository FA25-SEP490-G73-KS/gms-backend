package fpt.edu.vn.gms.common;


/**
 * CustomerLoyaltyLevel enum là loại cấp độ khách hàng
 */

public enum CustomerLoyaltyLevel {
    //NORMAL, VIP, VVIP
    NORMAL("Normal"),
    VIP("VIP"),
    VVIP("VVIP");
    private final String displayName;

    CustomerLoyaltyLevel(String displayName) {
        this.displayName = displayName;
    }

}

