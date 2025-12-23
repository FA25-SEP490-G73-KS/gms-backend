package fpt.edu.vn.gms.service.zalo;

import fpt.edu.vn.gms.dto.ZnsSendSurveyDTO;
import fpt.edu.vn.gms.dto.zalo.SendZnsPayload;
import fpt.edu.vn.gms.entity.Appointment;
import fpt.edu.vn.gms.entity.Debt;
import fpt.edu.vn.gms.entity.PriceQuotation;
import fpt.edu.vn.gms.entity.ServiceTicket;
import fpt.edu.vn.gms.utils.PhoneUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ZnsNotificationService {

    private final ZnsService znsService;
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

    @Value(("${zalo.vehicle-receipt.template-id:}"))
    private String vehicleReceiptTemplateId;

    @Value("${zalo.account-info.template-id:}")
    private String accountInfoTemplateId;

    @Value("${zalo.debt-notification.template-id:}")
    private String debtNotificationTemplateId;

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
                ? appointment.getCustomer().getFullName()
                : "Quý khách");

        templateData.put("appointment_code", appointment.getAppointmentCode());

        templateData.put("address", "110 đường Hoàng Nghiêu, phố Đông, phường Đông Tiến");

        templateData.put("license_plate", appointment.getVehicle().getLicensePlate());
        templateData.put("appointment_date", appointment.getAppointmentDate()
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

        templateData.put("schedule_time", appointment.getTimeSlot().getLabel());

        // Tạo OT token và đưa vào templateData để gửi
        createOTTokenAndPushOTTokenInTemplateData(templateData, SECONS_OF_ONE_WEEK);

        // Build payload
        SendZnsPayload payload = buildPayload(phone, templateData, appointmentConfirmationTemplateId);

        boolean success = znsService.sendZns(payload);

        if (!success) {
            throw new Exception("Failed to send appointment confirmation");
        } else {
            // Token đã được lưu trong createOTTokenAndPushOTTokenInTemplateData
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
                ? appointment.getCustomer().getFullName()
                : "Quý khách");

        templateData.put("address", "110 đường Hoàng Nghiêu, phố Đông, phường Đông Tiến");

        templateData.put("appointment_code", appointment.getAppointmentCode());

        // Tạo OT token và đưa vào templateData để gửi (với OT token reminder thì chỉ có
        // hạn là 24 tiếng)
        createOTTokenAndPushOTTokenInTemplateData(templateData, SECONS_OF_ONE_DAY);

        // Build payload
        SendZnsPayload payload = buildPayload(phone, templateData, appointmentReminderTemplateId);

        boolean success = znsService.sendZns(payload);

        if (!success) {
            throw new Exception("Failed to send appointment reminder");
        } else {
            // Token đã được lưu trong createOTTokenAndPushOTTokenInTemplateData
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
                ? ticket.getCustomer().getFullName()
                : "Quý khách");

        templateData.put("service_ticket_code", ticket.getServiceTicketCode());
        templateData.put("serviceTicket_id", ticket.getServiceTicketId());

        templateData.put("serviceTicketCode_path", ticket.getServiceTicketId().toString());

        templateData.put("delivery_at", ticket.getDeliveryAt().toString());

        // Định dạng tiền Việt Nam: 1.234.567
        DecimalFormat vnMoneyFormat = new DecimalFormat("#,###");
        String formattedAmount = vnMoneyFormat.format(quotation.getEstimateAmount()).replace(",", ".");
        templateData.put("estimate_amount", formattedAmount + " đ");

        templateData.put("phone", phone);

        // Tạo OT token và đưa vào templateData để gửi
        createOTTokenAndPushOTTokenInTemplateData(templateData, SECONS_OF_ONE_WEEK);

        // Build payload
        SendZnsPayload payload = buildPayload(phone, templateData, quotationTemplateId);

        boolean success = znsService.sendZns(payload);

        if (!success) {
            throw new Exception("Failed to send quotation notification");
        } else {
            // Token đã được lưu trong createOTTokenAndPushOTTokenInTemplateData
        }
    }

    /**
     * Send payment invoice with QR code information
     */
    public void sendPaymentInvoice(ServiceTicket ticket) throws Exception {
        String phone = ticket.getCustomer().getPhone();

        Map<String, Object> templateData = new HashMap<>();

        templateData.put("full_name", ticket.getCustomer().getFullName() != null
                ? ticket.getCustomer().getFullName()
                : "Quý khách");

        templateData.put("contract_number", "contract_num_test");

        templateData.put("estimate_amount", ticket.getPriceQuotation().getEstimateAmount().toString());

        templateData.put("transfer_amount", ticket.getPriceQuotation().getEstimateAmount().toString());

        templateData.put("service_ticket_id", ticket.getServiceTicketId().toString());

        templateData.put("license_plate", ticket.getVehicle().getLicensePlate());

        // Tạo OT token và đưa vào templateData để gửi
        createOTTokenAndPushOTTokenInTemplateData(templateData, SECONS_OF_ONE_WEEK);

        SendZnsPayload payload = buildPayload(phone, templateData, paymentTemplateId);

        boolean success = znsService.sendZns(payload);

        if (!success) {
            throw new Exception("Failed to send payment invoice");
        } else {
            // Token đã được lưu trong createOTTokenAndPushOTTokenInTemplateData
        }
    }

    /**
     * Send survey link after payment
     */
    public void sendSurveyLink(String phone, ZnsSendSurveyDTO znsSendSurveyDTO) throws Exception {

        Map<String, Object> templateData = new HashMap<>();

        templateData.put("full_name", znsSendSurveyDTO.getCustomerName() != null
                ? znsSendSurveyDTO.getCustomerName()
                : "Quý khách");

        templateData.put("car_model", znsSendSurveyDTO.getCarModel());

        templateData.put("license_plate", znsSendSurveyDTO.getLicensePlate());

        templateData.put("service_code", znsSendSurveyDTO.getServiceCode().toString());

        // Tạo OT token và đưa vào templateData để gửi
        createOTTokenAndPushOTTokenInTemplateData(templateData, SECONS_OF_ONE_WEEK);

        SendZnsPayload payload = buildPayload(phone, templateData, surveyTemplateId);

        boolean success = znsService.sendZns(payload);

        if (!success) {
            throw new Exception("Failed to send survey link");
        } else {
            // Token đã được lưu trong createOTTokenAndPushOTTokenInTemplateData
        }
    }

    public void sendVehicleReceiptNotification(ServiceTicket serviceTicket) throws Exception {
        Map<String, Object> templateData = new HashMap<>();

        String fullName = serviceTicket.getCustomer().getFullName();
        String phone = serviceTicket.getCustomer().getPhone();
        String licensePlate = serviceTicket.getVehicle().getLicensePlate();
        String receiptCode = serviceTicket.getServiceTicketCode().toString();

        templateData.put("customer_name", fullName != null ? fullName : "Quý khách");
        templateData.put("licensePlate", licensePlate);
        templateData.put("ma_don_hang", receiptCode);

        // Tạo OT token và đưa vào templateData để gửi
        createOTTokenAndPushOTTokenInTemplateData(templateData, SECONS_OF_ONE_WEEK);

        SendZnsPayload payload = buildPayload(phone, templateData, vehicleReceiptTemplateId);

        boolean success = znsService.sendZns(payload);

        if (!success) {
            throw new Exception("Failed to send vehicle receipt notification");
        } else {
            // Token đã được lưu trong createOTTokenAndPushOTTokenInTemplateData
        }
    }

    public void sendAccountInfoNotification(String phone, String customerName, String password) throws Exception {
        Map<String, Object> templateData = new HashMap<>();

        templateData.put("customer_name", customerName != null ? customerName : "Quý khách");
        // Giữ số điện thoại ở format Việt Nam (0986475989) để hiển thị trong template
        // cho người dùng
        templateData.put("account", phone);
        templateData.put("password", password);

        // Tạo OT token và đưa vào templateData để gửi
        createOTTokenAndPushOTTokenInTemplateData(templateData, SECONS_OF_ONE_WEEK);

        // Normalize số điện thoại sang format quốc tế (84986475989) để gửi qua ZNS API
        // Zalo chỉ nhận đầu số 84, nhưng trong template vẫn hiển thị format Việt Nam
        String normalizedPhoneForApi = PhoneUtils.normalize(phone);
        SendZnsPayload payload = buildPayload(normalizedPhoneForApi, templateData, accountInfoTemplateId);

        boolean success = znsService.sendZns(payload);

        if (!success) {
            throw new Exception("Failed to send account info notification");
        } else {
            // Token đã được lưu trong createOTTokenAndPushOTTokenInTemplateData
        }
    }

    /**
     * Send debt reminder notification to customer
     */
    public void sendDebtNotification(Debt debt) throws Exception {
        ServiceTicket serviceTicket = debt.getServiceTicket();
        String phone = serviceTicket.getCustomer().getPhone();

        Map<String, Object> templateData = new HashMap<>();

        // Customer name
        String customerName = serviceTicket.getCustomer().getFullName();
        templateData.put("customer_name", customerName != null ? customerName : "Quý khách");

        // Debt amount - format as Vietnamese currency (3.000.000)
        DecimalFormat vnMoneyFormat = new DecimalFormat("#,###");
        String formattedDebtAmount = vnMoneyFormat.format(debt.getAmount()).replace(",", ".");
        templateData.put("debt_mount", formattedDebtAmount);

        // Due date - format as dd-MM-yyyy (27-12-2025)
        String formattedDueDate = debt.getDueDate()
                .format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        templateData.put("due_date", formattedDueDate);

        // Service ticket code
        templateData.put("service_code", serviceTicket.getServiceTicketCode());

        // License plate
        templateData.put("licensePlate", serviceTicket.getVehicle().getLicensePlate());

        // Tạo OT token và đưa vào templateData để gửi
        createOTTokenAndPushOTTokenInTemplateData(templateData, SECONS_OF_ONE_WEEK);

        // Build payload
        SendZnsPayload payload = buildPayload(phone, templateData, debtNotificationTemplateId);

        boolean success = znsService.sendZns(payload);

        if (!success) {
            throw new Exception("Failed to send debt notification");
        } else {
            // Token đã được lưu trong createOTTokenAndPushOTTokenInTemplateData
        }
    }

    private SendZnsPayload buildPayload(String phone, Map<String, Object> templateData, String templateId) {

        return SendZnsPayload.builder()
                .phone(phone)
                .templateId(templateId)
                .templateData(templateData)
                .trackingId(UUID.randomUUID().toString())
                .build();
    }

    private String createOTTokenAndPushOTTokenInTemplateData(Map<String, Object> templateData, Long secondsToAdd) {
        // Tạo token ngắn để đưa vào payload, tránh giới hạn độ dài Zalo
        String shortToken = UUID.randomUUID().toString();

        // Lưu token + thời gian hết hạn vào DB
        this.oneTimeTokenService.saveToken(shortToken, secondsToAdd);

        // Đưa token vào template data với đúng key
        templateData.put("one_time_token", shortToken);

        // Trả về token để dùng nếu cần (hiện không parse JWT nữa)
        return shortToken;
    }
}