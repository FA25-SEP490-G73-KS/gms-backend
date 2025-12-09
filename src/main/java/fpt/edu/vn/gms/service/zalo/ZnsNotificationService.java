package fpt.edu.vn.gms.service.zalo;

import fpt.edu.vn.gms.dto.ZnsSendSurveyDTO;
import fpt.edu.vn.gms.dto.zalo.SendZnsPayload;
import fpt.edu.vn.gms.entity.Appointment;
import fpt.edu.vn.gms.entity.PriceQuotation;
import fpt.edu.vn.gms.entity.ServiceTicket;
import fpt.edu.vn.gms.service.auth.JwtService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ZnsNotificationService {

    private final ZnsService znsService;
    private final JwtService jwtService;
    private final OneTimeTokenService oneTimeTokenService;
    private Long SECONS_OF_ONE_WEEK = 604800L;
    private Long SECONS_OF_ONE_DAY = 86400L;

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

        SendZnsPayload payload = buildPayload(phone, templateData, otpTemplateId);

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

        templateData.put("full_name", appointment.getCustomer().getFullName() != null
                ? appointment.getCustomer().getFullName() : "Quý khách");

        templateData.put("appointment_code", appointment.getAppointmentCode());

        templateData.put("address", "110 đường Hoàng Nghiêu, phố Đông, phường Đông Tiến");

        templateData.put("license_plate", appointment.getVehicle().getLicensePlate());

        templateData.put("appointment_date", appointment.getAppointmentDate()
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

        templateData.put("schedule_time", appointment.getTimeSlot().getLabel());

        //Tạo OT token và đưa vào templateData để gửi
        String onTimeToken = createOTTokenAndPushOTTokenInTemplateData(templateData, SECONS_OF_ONE_WEEK);

        Date expirationFromOTToken = jwtService.extractClaim(onTimeToken, Claims::getExpiration);

        //Build payload
        SendZnsPayload payload = buildPayload(phone, templateData, appointmentConfirmationTemplateId);

        boolean success = znsService.sendZns(payload);

        if (!success) {
            throw new Exception("Failed to send appointment confirmation");
        }else{
            this.oneTimeTokenService.saveToken(onTimeToken, expirationFromOTToken.toString());
        }
    }

    /**
     * Send appointment reminder 24 hours before
     */
    public void sendAppointmentReminder(Appointment appointment) throws Exception {
        String phone = appointment.getCustomer().getPhone();

        Map<String, Object> templateData = new HashMap<>();

        templateData.put("appointment_date", appointment.getAppointmentDate()
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

        templateData.put("full_name", appointment.getCustomer().getFullName() != null
                ? appointment.getCustomer().getFullName() : "Quý khách");

        templateData.put("address", "110 đường Hoàng Nghiêu, phố Đông, phường Đông Tiến");

        templateData.put("appointment_code", appointment.getAppointmentCode());

        //Tạo OT token và đưa vào templateData để gửi (với OT token reminder thì chỉ có hạn là 24 tiếng)
        String onTimeToken = createOTTokenAndPushOTTokenInTemplateData(templateData, SECONS_OF_ONE_DAY);

        Date expirationFromOTToken = jwtService.extractClaim(onTimeToken, Claims::getExpiration);

        //Build payload
        SendZnsPayload payload = buildPayload(phone, templateData, appointmentReminderTemplateId);

        boolean success = znsService.sendZns(payload);

        if (!success) {
            throw new Exception("Failed to send appointment reminder");
        }
        else{
            this.oneTimeTokenService.saveToken(onTimeToken, expirationFromOTToken.toString());
        }
    }

    /**
     * Send quotation notification with link to view quotation
     */
    public void sendQuotationNotification(PriceQuotation quotation) throws Exception {
        ServiceTicket ticket = quotation.getServiceTicket();

        String phone = ticket.getCustomer().getPhone();

        Map<String, Object> templateData = new HashMap<>();

        templateData.put("license_plate", ticket.getVehicle().getLicensePlate());

        templateData.put("full_name", ticket.getCustomer().getFullName() != null
                ? ticket.getCustomer().getFullName() : "Quý khách");

        templateData.put("service_ticket_code", ticket.getServiceTicketCode().toString());

        templateData.put("serviceTicketCode_path", ticket.getServiceTicketCode().toString());

        templateData.put("delivery_at", ticket.getDeliveryAt().toString());

        templateData.put("estimate_amount", quotation.getEstimateAmount().toString());

        templateData.put("phone", phone);

        //Tạo OT token và đưa vào templateData để gửi
        String onTimeToken = createOTTokenAndPushOTTokenInTemplateData(templateData, SECONS_OF_ONE_WEEK);

        Date expirationFromOTToken = jwtService.extractClaim(onTimeToken, Claims::getExpiration);

        //Build payload
        SendZnsPayload payload = buildPayload(phone, templateData, quotationTemplateId);

        boolean success = znsService.sendZns(payload);

        if (!success) {
            throw new Exception("Failed to send quotation notification");
        }else{
            this.oneTimeTokenService.saveToken(onTimeToken, expirationFromOTToken.toString());
        }
    }

    /**
     * Send payment invoice with QR code information
     */
    public void sendPaymentInvoice(ServiceTicket ticket) throws Exception {
        String phone = ticket.getCustomer().getPhone();

        Map<String, Object> templateData = new HashMap<>();

        templateData.put("full_name", ticket.getCustomer().getFullName() != null
                ? ticket.getCustomer().getFullName() : "Quý khách");

        templateData.put("contract_number", "contract_num_test");

        templateData.put("estimate_amount", ticket.getPriceQuotation().getEstimateAmount().toString());

        templateData.put("transfer_amount", ticket.getPriceQuotation().getEstimateAmount().toString());

        templateData.put("service_ticket_id",  ticket.getServiceTicketId().toString());

        templateData.put("license_plate", ticket.getVehicle().getLicensePlate());

        //Tạo OT token và đưa vào templateData để gửi
        String onTimeToken = createOTTokenAndPushOTTokenInTemplateData(templateData, SECONS_OF_ONE_WEEK);

        Date expirationFromOTToken = jwtService.extractClaim(onTimeToken, Claims::getExpiration);

        SendZnsPayload payload = buildPayload(phone, templateData, paymentTemplateId);

        boolean success = znsService.sendZns(payload);

        if (!success) {
            throw new Exception("Failed to send payment invoice");
        }
        else{
            this.oneTimeTokenService.saveToken(onTimeToken, expirationFromOTToken.toString());
        }
    }

    /**
     * Send survey link after payment
     */
    public void sendSurveyLink(String phone, ZnsSendSurveyDTO znsSendSurveyDTO) throws Exception {

        Map<String, Object> templateData = new HashMap<>();

        templateData.put("full_name", znsSendSurveyDTO.getCustomerName() != null
                ? znsSendSurveyDTO.getCustomerName() : "Quý khách");

        templateData.put("car_model", znsSendSurveyDTO.getCarModel());

        templateData.put("license_plate", znsSendSurveyDTO.getLicensePlate());

        templateData.put("service_code", znsSendSurveyDTO.getServiceCode().toString());

        //Tạo OT token và đưa vào templateData để gửi
        String onTimeToken = createOTTokenAndPushOTTokenInTemplateData(templateData, SECONS_OF_ONE_WEEK);

        Date expirationFromOTToken = jwtService.extractClaim(onTimeToken, Claims::getExpiration);

        SendZnsPayload payload = buildPayload(phone, templateData, surveyTemplateId);

        boolean success = znsService.sendZns(payload);

        if (!success) {
            throw new Exception("Failed to send survey link");
        } else{
            this.oneTimeTokenService.saveToken(onTimeToken, expirationFromOTToken.toString());
        }
    }


    private SendZnsPayload buildPayload(String phone, Map<String, Object> templateData, String templateId){

        return SendZnsPayload.builder()
                .phone(phone)
                .templateId(templateId)
                .templateData(templateData)
                .trackingId(UUID.randomUUID().toString())
                .build();
    }

    private String createOTTokenAndPushOTTokenInTemplateData(Map<String, Object> templateData, Long secondsToAdd) {
        // Generation one-time token
        String onTimeToken = jwtService.generateOneTimeToken(templateData, secondsToAdd);

        templateData.put("on_time_token", onTimeToken);

        return onTimeToken;

    }
}

