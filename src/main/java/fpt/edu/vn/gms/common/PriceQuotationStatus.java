package fpt.edu.vn.gms.common;

public enum PriceQuotationStatus {
    DRAFT("Nháp"),
    SENT_TO_CUSTOMER("Đã gửi khách hàng"),
    APPROVED("Đã duyệt"),
    REJECTED("Bị từ chối"),
    FINALIZED("Hoàn tất");

    private final String displayName;

    PriceQuotationStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
