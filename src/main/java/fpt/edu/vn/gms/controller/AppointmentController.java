package fpt.edu.vn.gms.controller;

import fpt.edu.vn.gms.common.AppointmentStatus;
import fpt.edu.vn.gms.dto.request.AppointmentRequestDto;
import fpt.edu.vn.gms.dto.request.ServiceTicketRequestDto;
import fpt.edu.vn.gms.dto.response.AppointmentResponseDto;
import fpt.edu.vn.gms.dto.response.ApiResponse;
import fpt.edu.vn.gms.dto.response.ServiceTicketResponseDto;
import fpt.edu.vn.gms.dto.response.TimeSlotDto;
import fpt.edu.vn.gms.service.AppointmentService;
import fpt.edu.vn.gms.service.ServiceTicketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@CrossOrigin(origins = "${fe-local-host}")
@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
public class AppointmentController {

    private final AppointmentService service;

    @GetMapping("/time-slots")
    @Operation(
            summary = "Get available time slots by date",
            description = "Retrieves all available time slots for a given date"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved time slots",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid date format"
            )
    })
    public ResponseEntity<ApiResponse<List<TimeSlotDto>>> getSlots(
            @Parameter(description = "Date for which to retrieve available slots", example = "2025-10-21")
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        List<TimeSlotDto> slots = service.getTimeSlotsByDate(date);
        return ResponseEntity.ok(ApiResponse.success("Retrieved available time slots", slots));
    }

    // =============================
    // Create a new appointment
    // =============================
    @PostMapping
    @Operation(
            summary = "Create a new appointment",
            description = "Creates a new appointment with provided details"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "201",
                    description = "Appointment created successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ApiResponse.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid appointment data provided"
            )
    })
    public ResponseEntity<ApiResponse<AppointmentResponseDto>> createAppointment(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Appointment details to create",
                    required = true,
                    content = @Content(schema = @Schema(implementation = AppointmentRequestDto.class))
            )
            @RequestBody AppointmentRequestDto dto
    ) {
        AppointmentResponseDto response = service.createAppointment(dto);
        return ResponseEntity.status(201)
                .body(ApiResponse.created("Appointment created successfully", response));
    }

    // =============================
    // Get all appointments (paginated)
    // =============================
    @GetMapping
    @Operation(
            summary = "Get all appointments",
            description = "Retrieves a paginated list of all appointments"
    )
    public ResponseEntity<ApiResponse<Page<AppointmentResponseDto>>> getAllAppointments(
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "6")
            @RequestParam(defaultValue = "6") int size
    ) {
        Page<AppointmentResponseDto> appointments = service.getAllAppointments(page, size);
        return ResponseEntity.ok(ApiResponse.success("Appointments fetched successfully", appointments));
    }

    // =============================
    // Get appointment by ID
    // =============================
    @GetMapping("/{id}")
    @Operation(
            summary = "Get appointment by ID",
            description = "Retrieves a specific appointment by its unique identifier"
    )
    public ResponseEntity<ApiResponse<AppointmentResponseDto>> getAppointmentById(
            @Parameter(description = "Unique identifier of the appointment", required = true, example = "1")
            @PathVariable Long id
    ) {
        AppointmentResponseDto appointment = service.getAppointmentById(id);
        return ResponseEntity.ok(ApiResponse.success("Appointment found", appointment));
    }

    // =============================
    // Update appointment status
    // =============================
    @PatchMapping("/{id}/status")
    @Operation(
            summary = "Update appointment status",
            description = "Updates the status of a specific appointment"
    )
    public ResponseEntity<ApiResponse<AppointmentResponseDto>> updateStatus(
            @Parameter(description = "Unique identifier of the appointment", example = "1")
            @PathVariable Long id,
            @Parameter(description = "New appointment status", example = "CONFIRMED")
            @RequestParam AppointmentStatus status
    ) {
        AppointmentResponseDto updated = service.updateStatus(id, status);
        return ResponseEntity.ok(ApiResponse.success("Appointment status updated", updated));
    }

}
