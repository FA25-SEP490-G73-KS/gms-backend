package fpt.edu.vn.gms.controller;

import fpt.edu.vn.gms.dto.ZnsSendSurveyDTO;
import fpt.edu.vn.gms.dto.response.ApiResponse;
import fpt.edu.vn.gms.entity.Appointment;
import fpt.edu.vn.gms.entity.PriceQuotation;
import fpt.edu.vn.gms.entity.ServiceTicket;
import fpt.edu.vn.gms.exception.ResourceNotFoundException;
import fpt.edu.vn.gms.repository.AppointmentRepository;
import fpt.edu.vn.gms.repository.PriceQuotationRepository;
import fpt.edu.vn.gms.repository.ServiceTicketRepository;
import fpt.edu.vn.gms.service.zalo.ZnsNotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/zns")
@RequiredArgsConstructor
@Tag(name = "ZNS Notification", description = "APIs for triggering ZNS notifications manually")
public class ZnsNotificationController {

    private final ZnsNotificationService znsNotificationService;
    private final AppointmentRepository appointmentRepository;
    private final PriceQuotationRepository priceQuotationRepository;
    private final ServiceTicketRepository serviceTicketRepository;

    @PostMapping("/appointment/{appointmentId}/reminder")
    @Operation(summary = "Send appointment reminder", description = "Manually trigger appointment reminder notification")
    public ResponseEntity<ApiResponse<String>> sendAppointmentReminder(@PathVariable Long appointmentId) {
        try {
            Appointment appointment = appointmentRepository.findById(appointmentId)
                    .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));
            znsNotificationService.sendAppointmentReminder(appointment);
            return ResponseEntity.ok(ApiResponse.success("Reminder sent successfully", "OK"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, "Failed to send reminder: " + e.getMessage()));
        }
    }

    @PostMapping("/appointment/{appointmentId}/notification")
    @Operation(summary = "Send appointment notification", description = "Manually trigger appointment notification")
    public ResponseEntity<ApiResponse<String>> sendAppointmentNotification(@PathVariable Long appointmentId) {
        try {
            Appointment appointment = appointmentRepository.findById(appointmentId)
                    .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));
            znsNotificationService.sendAppointmentConfirmation(appointment);
            return ResponseEntity.ok(ApiResponse.success("Notification sent successfully", "OK"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, "Failed to send notification: " + e.getMessage()));
        }
    }

    @PostMapping("/quotation/{quotationId}/send")
    @Operation(summary = "Send quotation notification", description = "Send quotation notification to customer via ZNS")
    public ResponseEntity<ApiResponse<String>> sendQuotationNotification(@PathVariable Long quotationId) {
        try {
            PriceQuotation quotation = priceQuotationRepository.findById(quotationId)
                    .orElseThrow(() -> new ResourceNotFoundException("Quotation not found"));

            znsNotificationService.sendQuotationNotification(quotation);

            return ResponseEntity.ok(ApiResponse.success("Quotation notification sent successfully", "OK"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, "Failed to send quotation notification: " + e.getMessage()));
        }
    }

    @PostMapping("/payment/{ticketId}/invoice")
    @Operation(summary = "Send payment invoice", description = "Send payment invoice with QR code via ZNS")
    public ResponseEntity<ApiResponse<String>> sendPaymentInvoice(
            @PathVariable Long ticketId) {

        try {
            ServiceTicket ticket = serviceTicketRepository.findById(ticketId)
                    .orElseThrow(() -> new ResourceNotFoundException("Service ticket not found"));

            znsNotificationService.sendPaymentInvoice(ticket);

            return ResponseEntity.ok(ApiResponse.success("Payment invoice sent successfully", "OK"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, "Failed to send payment invoice: " + e.getMessage()));
        }
    }

    @PostMapping("/survey/{ticketId}/send")
    @Operation(summary = "Send survey link", description = "Send survey link to customer after payment")
    public ResponseEntity<ApiResponse<String>> sendSurveyLink(@PathVariable Long ticketId) {
        try {
            ServiceTicket ticket = serviceTicketRepository.findById(ticketId)
                    .orElseThrow(() -> new ResourceNotFoundException("Service ticket not found"));

            // TODO : use Mapper
            ZnsSendSurveyDTO znsSendSurveyDTO = new ZnsSendSurveyDTO();

            znsSendSurveyDTO.setCustomerName(ticket.getCustomer().getFullName());
            znsSendSurveyDTO.setCarModel(ticket.getVehicle().getVehicleModel().getName());
            znsSendSurveyDTO.setLicensePlate(ticket.getVehicle().getLicensePlate());
            znsSendSurveyDTO.setServiceCode(ticket.getServiceTicketId());

            znsNotificationService.sendSurveyLink(ticket.getCustomer().getPhone(), znsSendSurveyDTO);
            return ResponseEntity.ok(ApiResponse.success("Survey link sent successfully", "OK"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, "Failed to send survey link: " + e.getMessage()));
        }
    }
}

