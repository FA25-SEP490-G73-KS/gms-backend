package fpt.edu.vn.gms.common;

public enum PartStatus {

    AVAILABLE("Có sẵn trong kho, giá chính xác"),
    OUT_OF_STOCK("Đã biết thông tin, có giá nhưng hết hàng"),
    UNKNOWN("Chưa có thông tin, chưa rõ giá, chưa tồn kho");

    private final String description;
    PartStatus(String description) { this.description = description; }
    public String getDescription() { return description; }
}
