package fpt.edu.vn.gms.common;

public enum CustomerType {
    //NORMAL, VIP, VVIP\
    NORMAL("Normal"),
    VIP("VIP"),
    VVIP("VVIP");
    private final String displayName;

    CustomerType(String displayName) {
        this.displayName = displayName;
    }
}
