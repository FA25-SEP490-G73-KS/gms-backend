package fpt.edu.vn.gms.service;

import fpt.edu.vn.gms.common.NotificationType;
import fpt.edu.vn.gms.dto.response.NotificationResponseDto;
import fpt.edu.vn.gms.entity.Notification;

import java.util.List;

public interface NotificationService {

    NotificationResponseDto createNotification(String recipientPhone,
                                    String title,
                                    String message,
                                    NotificationType type);

//    void notifyQuotationRejectedByCustomer(PriceQuotation quotation, String reason);
//
//    void notifyQuotationConfirmedByCustomer(PriceQuotation quotation);

    List<NotificationResponseDto> getNotificationsForUser(String recipientPhone);
}
