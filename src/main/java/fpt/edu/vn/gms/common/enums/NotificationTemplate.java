package fpt.edu.vn.gms.common.enums;

import lombok.Getter;

public enum NotificationTemplate {
    PRICE_QUOTATION_APPROVED("Báo giá #%d đã được phê duyệt", "Xem báo giá"),
    PRICE_QUOTATION_REJECTED("Báo giá #%d đã bị từ chối", "Xem báo giá");

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
