package fpt.edu.vn.gms.common;

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