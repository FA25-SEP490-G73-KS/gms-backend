package fpt.edu.vn.gms.service.zalo;

import fpt.edu.vn.gms.dto.ZnsSendSurveyDTO;
import fpt.edu.vn.gms.dto.zalo.SendZnsPayload;
import fpt.edu.vn.gms.entity.Appointment;
import fpt.edu.vn.gms.entity.PriceQuotation;
import fpt.edu.vn.gms.entity.ServiceTicket;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ZnsNotificationService {

    private final ZnsService znsService;

    @Value("${zalo.otp.template-id:}")
    private String otpTemplateId;

    @Value("${zalo.appointment-confirmation.template-id:}")
    private String appointmentConfirmationTemplateId;

    @Value("${zalo.appointment-reminder.template-id:}")
    private String appointmentReminderTemplateId;

    @Value("${zalo.quotation.template-id:}")
    private String quotationTemplateId;

    @Value("${zalo.payment.template-id:}")
    private String paymentTemplateId;

    @Value("${zalo.survey.template-id:}")
    private String surveyTemplateId;

    @Value("${app.frontend-url:https://yourdomain.com}")
    private String frontendUrl;

    /**
     * Send OTP notification via ZNS
     */
    public void sendOtpNotification(String phone, String otpCode, String templateId) throws Exception {
        Map<String, Object> templateData = new HashMap<>();
        templateData.put("otp", otpCode);

        SendZnsPayload payload = SendZnsPayload.builder()
                .phone(phone)
                .templateId(templateId != null && !templateId.isEmpty() ? templateId : otpTemplateId)
                .templateData(templateData)
                .trackingId(UUID.randomUUID().toString())
                .build();

        boolean success = znsService.sendZns(payload);
        if (!success) {
            throw new Exception("Failed to send OTP notification");
        }
    }

    /**
     * Send appointment confirmation notification
     */
    public void sendAppointmentConfirmation(Appointment appointment) throws Exception {
        String phone = appointment.getCustomer().getPhone();

        Map<String, Object> templateData = new HashMap<>();

        templateData.put("customer_name", appointment.getCustomer().getFullName() != null
                ? appointment.getCustomer().getFullName() : "Quý khách");

        templateData.put("booking_code", appointment.getAppointmentId().toString());

        templateData.put("address", "110 đường Hoàng Nghiêu, phố Đông, phường Đông Tiến");

        templateData.put("license_plate", appointment.getVehicle().getLicensePlate());

        templateData.put("schedule_date", appointment.getAppointmentDate()
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

        templateData.put("schedule_time", appointment.getTimeSlot().getLabel());

        SendZnsPayload payload = SendZnsPayload.builder()
                .phone(phone)
                .templateId(appointmentConfirmationTemplateId)
                .templateData(templateData)
                .trackingId(UUID.randomUUID().toString())
                .build();

        boolean success = znsService.sendZns(payload);
        if (!success) {
            throw new Exception("Failed to send appointment confirmation");
        }
    }

    /**
     * Send appointment reminder 24 hours before
     */
    public void sendAppointmentReminder(Appointment appointment) throws Exception {
        String phone = appointment.getCustomer().getPhone();

        Map<String, Object> templateData = new HashMap<>();

        templateData.put("schedule_time", appointment.getAppointmentDate()
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

        templateData.put("customer_name", appointment.getCustomer().getFullName() != null
                ? appointment.getCustomer().getFullName() : "Quý khách");

        templateData.put("address", "110 đường Hoàng Nghiêu, phố Đông, phường Đông Tiến");

        templateData.put("booking_code", appointment.getAppointmentId().toString());

        SendZnsPayload payload = SendZnsPayload.builder()
                .phone(phone)
                .templateId(appointmentReminderTemplateId)
                .templateData(templateData)
                .trackingId(UUID.randomUUID().toString())
                .build();

        boolean success = znsService.sendZns(payload);
        if (!success) {
            throw new Exception("Failed to send appointment reminder");
        }
    }

    /**
     * Send quotation notification with link to view quotation
     */
    public void sendQuotationNotification(PriceQuotation quotation) throws Exception {
        ServiceTicket ticket = quotation.getServiceTicket();

        String phone = ticket.getCustomer().getPhone();

        Map<String, Object> templateData = new HashMap<>();

        templateData.put("car_plate", ticket.getVehicle().getLicensePlate());

        templateData.put("customer_name", ticket.getCustomer().getFullName() != null
                ? ticket.getCustomer().getFullName() : "Quý khách");

        templateData.put("serviceticket_code", ticket.getServiceTicketId().toString());

        templateData.put("expected_date", ticket.getDeliveryAt().toString());

        templateData.put("total_price", quotation.getEstimateAmount().toString());

        templateData.put("customer_phone", phone);

        SendZnsPayload payload = SendZnsPayload.builder()
                .phone(phone)
                .templateId(quotationTemplateId)
                .templateData(templateData)
                .trackingId(UUID.randomUUID().toString())
                .build();

        boolean success = znsService.sendZns(payload);
        if (!success) {
            throw new Exception("Failed to send quotation notification");
        }
    }

    /**
     * Send payment invoice with QR code information
     */
    public void sendPaymentInvoice(ServiceTicket ticket) throws Exception {
        String phone = ticket.getCustomer().getPhone();

        Map<String, Object> templateData = new HashMap<>();

        templateData.put("customer_name", ticket.getCustomer().getFullName() != null
                ? ticket.getCustomer().getFullName() : "Quý khách");

        templateData.put("contract_number", "contract_num_test");

        templateData.put("price", ticket.getPriceQuotation().getEstimateAmount().toString());

        templateData.put("transfer_amount", ticket.getPriceQuotation().getEstimateAmount().toString());

        templateData.put("bank_transfer_note", "Thanh toán dịch vụ - Mã phiếu: " + ticket.getServiceTicketId());

        templateData.put("vehicle_license_plate_", ticket.getVehicle().getLicensePlate());

        SendZnsPayload payload = SendZnsPayload.builder()
                .phone(phone)
                .templateId(paymentTemplateId)
                .templateData(templateData)
                .trackingId(UUID.randomUUID().toString())
                .build();

        boolean success = znsService.sendZns(payload);
        if (!success) {
            throw new Exception("Failed to send payment invoice");
        }
    }

    /**
     * Send survey link after payment
     */
    public void sendSurveyLink(String phone, ZnsSendSurveyDTO znsSendSurveyDTO) throws Exception {

        Map<String, Object> templateData = new HashMap<>();

        templateData.put("customer_name", znsSendSurveyDTO.getCustomerName() != null
                ? znsSendSurveyDTO.getCustomerName() : "Quý khách");

        templateData.put("car_model", znsSendSurveyDTO.getCarModel());

        templateData.put("license_plate", znsSendSurveyDTO.getLicensePlate());

        templateData.put("service_code", znsSendSurveyDTO.getServiceCode().toString());

        SendZnsPayload payload = SendZnsPayload.builder()
                .phone(phone)
                .templateId(surveyTemplateId)
                .templateData(templateData)
                .trackingId(UUID.randomUUID().toString())
                .build();

        boolean success = znsService.sendZns(payload);
        if (!success) {
            throw new Exception("Failed to send survey link");
        }
    }

    private String formatCurrency(BigDecimal amount) {
        if (amount == null) {
            return "0 đ";
        }
        return String.format("%,d đ", amount.longValue());
    }
}

