package fpt.edu.vn.gms.controller;

import fpt.edu.vn.gms.common.annotations.Public;
import fpt.edu.vn.gms.dto.ZnsAppointmentInfo;
import fpt.edu.vn.gms.dto.ZnsSendSurveyDTO;
import fpt.edu.vn.gms.dto.response.ApiResponse;
import fpt.edu.vn.gms.entity.Appointment;
import fpt.edu.vn.gms.entity.PriceQuotation;
import fpt.edu.vn.gms.entity.ServiceTicket;
import fpt.edu.vn.gms.exception.ResourceNotFoundException;
import fpt.edu.vn.gms.repository.AppointmentRepository;
import fpt.edu.vn.gms.repository.PriceQuotationRepository;
import fpt.edu.vn.gms.repository.ServiceTicketRepository;
import fpt.edu.vn.gms.service.AppointmentService;
import fpt.edu.vn.gms.service.auth.JwtService;
import fpt.edu.vn.gms.service.zalo.ZnsNotificationService;
import io.jsonwebtoken.Claims;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;

import static fpt.edu.vn.gms.utils.AppRoutes.ZNS_NOTIFICATIONS_PREFIX;

@Tag(name = "zns-notifications", description = "Gửi thông báo ZNS (Zalo Notification Service) thủ công")
@CrossOrigin(origins = "${fe-local-host}")
@RestController
@RequestMapping(path = ZNS_NOTIFICATIONS_PREFIX, produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class ZnsNotificationController {

    private final ZnsNotificationService znsNotificationService;
    private final AppointmentService appointmentService;
    private final JwtService jwtService;
    private final AppointmentRepository appointmentRepository;
    private final PriceQuotationRepository priceQuotationRepository;
    private final ServiceTicketRepository serviceTicketRepository;

    @Public
    @PostMapping("/appointment/{appointmentId}/reminder")
    @Operation(summary = "Gửi lời nhắc cuộc hẹn", description = "Gửi thông báo nhắc nhở cuộc hẹn một cách thủ công.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Gửi lời nhắc thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Không thể gửi lời nhắc", content = @Content(schema = @Schema(hidden = true))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy cuộc hẹn", content = @Content(schema = @Schema(hidden = true))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Lỗi máy chủ nội bộ", content = @Content(schema = @Schema(hidden = true)))
    })
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

    @Public
    @PostMapping("/appointment/{appointmentId}/notification")
    @Operation(summary = "Gửi thông báo cuộc hẹn", description = "Gửi thông báo xác nhận cuộc hẹn một cách thủ công.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Gửi thông báo thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Không thể gửi thông báo", content = @Content(schema = @Schema(hidden = true))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy cuộc hẹn", content = @Content(schema = @Schema(hidden = true))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Lỗi máy chủ nội bộ", content = @Content(schema = @Schema(hidden = true)))
    })
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

    @Public
    @PostMapping("/quotation/{quotationId}/send")
    @Operation(summary = "Gửi thông báo báo giá", description = "Gửi thông báo báo giá cho khách hàng qua ZNS.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Gửi thông báo báo giá thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Không thể gửi thông báo báo giá", content = @Content(schema = @Schema(hidden = true))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy báo giá", content = @Content(schema = @Schema(hidden = true))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Lỗi máy chủ nội bộ", content = @Content(schema = @Schema(hidden = true)))
    })
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

    @Public
    @PostMapping("/payment/{ticketId}/invoice")
    @Operation(summary = "Gửi hóa đơn thanh toán", description = "Gửi hóa đơn thanh toán với mã QR qua ZNS.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Gửi hóa đơn thanh toán thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Không thể gửi hóa đơn thanh toán", content = @Content(schema = @Schema(hidden = true))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy phiếu dịch vụ", content = @Content(schema = @Schema(hidden = true))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Lỗi máy chủ nội bộ", content = @Content(schema = @Schema(hidden = true)))
    })
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

    @Public
    @PostMapping("/survey/{ticketId}/send")
    @Operation(summary = "Gửi liên kết khảo sát", description = "Gửi liên kết khảo sát cho khách hàng sau khi thanh toán.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Gửi liên kết khảo sát thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Không thể gửi liên kết khảo sát", content = @Content(schema = @Schema(hidden = true))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy phiếu dịch vụ", content = @Content(schema = @Schema(hidden = true))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Lỗi máy chủ nội bộ", content = @Content(schema = @Schema(hidden = true)))
    })
    public ResponseEntity<ApiResponse<String>> sendSurveyLink(@PathVariable Long ticketId) {
        try {
            ServiceTicket ticket = serviceTicketRepository.findById(ticketId)
                    .orElseThrow(() -> new ResourceNotFoundException("Service ticket not found"));

            // TODO : use Mapper
            ZnsSendSurveyDTO znsSendSurveyDTO = new ZnsSendSurveyDTO();

            znsSendSurveyDTO.setCustomerName(ticket.getCustomer().getFullName());
            znsSendSurveyDTO.setCarModel(ticket.getVehicle().getVehicleModel().getName());
            znsSendSurveyDTO.setLicensePlate(ticket.getVehicle().getLicensePlate());
            znsSendSurveyDTO.setServiceId(ticket.getServiceTicketId());
            znsSendSurveyDTO.setServiceCode(ticket.getServiceTicketCode());

            znsNotificationService.sendSurveyLink(ticket.getCustomer().getPhone(), znsSendSurveyDTO);
            return ResponseEntity.ok(ApiResponse.success("Survey link sent successfully", "OK"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, "Failed to send survey link: " + e.getMessage()));
        }
    }

    @Public
    @PostMapping("/vehicle-receipt/{ticketId}/send")
    @Operation(summary = "Gửi thông báo nhận xe", description = "Gửi thông báo nhận xe cho khách hàng qua ZNS.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Gửi thông báo nhận xe thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Không thể gửi thông báo nhận xe", content = @Content(schema = @Schema(hidden = true))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Không tìm thấy phiếu dịch vụ", content = @Content(schema = @Schema(hidden = true))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Lỗi máy chủ nội bộ", content = @Content(schema = @Schema(hidden = true)))
    })
    public ResponseEntity<ApiResponse<String>> sendVehicleReceiptNotification(@PathVariable Long ticketId) {
        try {
            ServiceTicket ticket = serviceTicketRepository.findById(ticketId)
                    .orElseThrow(() -> new ResourceNotFoundException("Service ticket not found"));

            znsNotificationService.sendVehicleReceiptNotification(ticket);

            return ResponseEntity.ok(ApiResponse.success("Vehicle receipt notification sent successfully", "OK"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(400, "Failed to send vehicle receipt notification: " + e.getMessage()));
        }
    }

}
