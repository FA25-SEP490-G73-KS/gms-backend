package fpt.edu.vn.gms.common;

public enum CustomerType {
    //Doanh nghiệp, Cá nhân
    DOANH_NGHIEP("doanh nghiệp"),
    CA_NHAN("cá nhân");
    private final String displayName;

    CustomerType(String displayName) {
        this.displayName = displayName;
    }

}