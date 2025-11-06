package fpt.edu.vn.gms.service.impl;

import fpt.edu.vn.gms.common.NotificationType;
import fpt.edu.vn.gms.dto.response.NotificationResponseDto;
import fpt.edu.vn.gms.entity.Notification;
import fpt.edu.vn.gms.entity.PriceQuotation;
import fpt.edu.vn.gms.entity.ServiceTicket;
import fpt.edu.vn.gms.mapper.NotificationMapper;
import fpt.edu.vn.gms.repository.NotificationRepository;
import fpt.edu.vn.gms.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;


    public void notifyQuotationConfirmedByCustomer(PriceQuotation quotation) {
        ServiceTicket serviceTicket = quotation.getServiceTicket();
        if (serviceTicket == null || serviceTicket.getCreatedBy() == null) return;

        String recipientPhone = serviceTicket.getCreatedBy().getPhone();

        Notification notification = Notification.builder()
                .title("Khách hàng đã xác nhận báo giá")
                .message(String.format(
                        "Khách hàng đã đồng ý báo giá #%d trong phiếu dịch vụ #%d.",
                        quotation.getPriceQuotationId(),
                        serviceTicket.getServiceTicketId()
                ))
                .recipientPhone(recipientPhone)
                .relatedServiceTicketId(serviceTicket.getServiceTicketId())
                .relatedQuotationId(quotation.getPriceQuotationId())
                .type(NotificationType.QUOTATION_CONFIRMED)
                .createdAt(LocalDateTime.now())
                .isRead(false)
                .build();

        notificationRepository.save(notification);
    }


    public void notifyQuotationRejectedByCustomer(PriceQuotation quotation, String reason) {
        ServiceTicket serviceTicket = quotation.getServiceTicket();
        if (serviceTicket == null || serviceTicket.getCreatedBy() == null) return;

        String recipientPhone = serviceTicket.getCreatedBy().getPhone();

        Notification notification = Notification.builder()
                .title("Khách hàng đã từ chối báo giá")
                .message(String.format(
                        "Khách hàng đã từ chối báo giá #%d trong phiếu dịch vụ #%d. %s",
                        quotation.getPriceQuotationId(),
                        serviceTicket.getServiceTicketId(),
                        reason != null ? "Lý do: " + reason : ""
                ))
                .recipientPhone(recipientPhone)
                .relatedServiceTicketId(serviceTicket.getServiceTicketId())
                .relatedQuotationId(quotation.getPriceQuotationId())
                .type(NotificationType.QUOTATION_REJECTED)
                .createdAt(LocalDateTime.now())
                .isRead(false)
                .build();

        notificationRepository.save(notification);
    }

    @Override
    public List<NotificationResponseDto> getNotificationsForUser(String recipientPhone) {

        return notificationRepository.findByRecipientPhoneOrderByCreatedAtDesc(recipientPhone)
                .stream()
                .map(notificationMapper::toResponseDto)
                .toList();
    }
}
