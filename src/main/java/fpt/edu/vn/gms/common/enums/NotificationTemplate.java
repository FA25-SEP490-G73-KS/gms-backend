package fpt.edu.vn.gms.common.enums;

import lombok.Getter;

public enum NotificationTemplate {
    PRICE_QUOTATION_APPROVED("Báo giá #%d đã được phê duyệt", "Xem báo giá"),
    PRICE_QUOTATION_REJECTED("Báo giá #%d đã bị từ chối", "Xem báo giá"),
    PURCHASE_REQUEST_CONFIRMED("Phiếu mua hàng #%d đã được duyệt", "Xem phiếu mua hàng"),
    PURCHASE_REQUEST_REJECTED("Phiếu mua hàng #%d đã bị từ chối", "Xem phiếu mua hàng"),
    STOCK_RECEIPT_ITEM_RECEIVED(
    "Linh kiện đã về hàng: %s",
            "Linh kiện '%s' của báo giá #%d đã được nhập kho."
    );

    @Getter
    private final String title;
    private final String messageTemplate;

    NotificationTemplate(String title, String messageTemplate) {
        this.title = title;
        this.messageTemplate = messageTemplate;
    }

    public String format(Object... args) {
        return String.format(messageTemplate, args);
    }
}

