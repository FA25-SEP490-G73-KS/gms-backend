package fpt.edu.vn.gms.common;

public enum QuotationItemStatus {

    TEMPORARY("Báo giá tạm, chưa xác nhận giá"),
    ACTIVE("Đã có giá, chờ duyệt"),
    OUT_OF_STOCK("Hết hàng, chưa thể xác nhận"),
    CONFIRMED("Đã duyệt, xác nhận dùng chính thức");

    private final String description;
    QuotationItemStatus(String description) { this.description = description; }
    public String getDescription() { return description; }
}
