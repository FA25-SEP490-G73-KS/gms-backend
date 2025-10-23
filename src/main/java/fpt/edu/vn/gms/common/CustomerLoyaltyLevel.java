package fpt.edu.vn.gms.common;

public enum CustomerLoyaltyLevel {
    //Doanh nghiệp, Cá nhân
    DOANH_NGHIEP("doanh nghiệp"),
    CA_NHAN("cá nhân");
    private final String displayName;

    CustomerLoyaltyLevel(String displayName) {
        this.displayName = displayName;
    }
}
