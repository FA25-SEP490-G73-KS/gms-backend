package fpt.edu.vn.gms.service;

import fpt.edu.vn.gms.common.NotificationType;
import fpt.edu.vn.gms.dto.response.NotificationResponseDto;
import fpt.edu.vn.gms.entity.Notification;
import fpt.edu.vn.gms.entity.PriceQuotation;

import java.util.List;

public interface NotificationService {

    Notification createNotification(String recipientPhone,
                                    String title,
                                    String message,
                                    NotificationType type,
                                    String serviceTicketCode);

//    void notifyQuotationRejectedByCustomer(PriceQuotation quotation, String reason);
//
//    void notifyQuotationConfirmedByCustomer(PriceQuotation quotation);

    List<NotificationResponseDto> getNotificationsForUser(String recipientPhone);
}
