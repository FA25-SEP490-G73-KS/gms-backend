package fpt.edu.vn.gms.common;

/**
 * CustomerType là loại hình khách hàng
 */
public enum CustomerType {
    //Doanh nghiệp, Cá nhân
    DOANH_NGHIEP("doanh nghiệp"),
    CA_NHAN("cá nhân");
    private final String displayName;

    CustomerType(String displayName) {
        this.displayName = displayName;
    }

}
