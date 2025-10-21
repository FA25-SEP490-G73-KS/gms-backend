package fpt.edu.vn.gms.controller;

import fpt.edu.vn.gms.common.AppointmentStatus;
import fpt.edu.vn.gms.dto.AppointmentRequestDto;
import fpt.edu.vn.gms.dto.AppointmentResponseDto;
import fpt.edu.vn.gms.dto.TimeSlotDto;
import fpt.edu.vn.gms.service.AppointmentService;
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

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
@Tag(name = "Appointment Management", description = "Controller for managing appointments")
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
                            schema = @Schema(implementation = TimeSlotDto.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid date format"
            )
    })
    public List<TimeSlotDto> getSlots(
            @Parameter(description = "Date for which to retrieve available slots", required = true, example = "2025-10-21")
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        return service.getTimeSlotsByDate(date);
    }

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
                            schema = @Schema(implementation = AppointmentResponseDto.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid appointment data provided"
            )
    })
    public AppointmentResponseDto createAppointment(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Appointment details to create",
                    required = true,
                    content = @Content(schema = @Schema(implementation = AppointmentRequestDto.class))
            )
            @RequestBody AppointmentRequestDto dto
    ) {
        return service.createAppointment(dto);
    }

    @GetMapping
    @Operation(
            summary = "Get all appointments",
            description = "Retrieves a paginated list of all appointments"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved appointments",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AppointmentResponseDto.class))
            )
    })
    public ResponseEntity<Page<AppointmentResponseDto>> getAllAppointments(
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size", example = "6")
            @RequestParam(defaultValue = "6") int size
    ) {
        return ResponseEntity.ok(service.getAllAppointments(page, size));
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get appointment by ID",
            description = "Retrieves a specific appointment by its unique identifier"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Successfully retrieved appointment",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AppointmentResponseDto.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Appointment not found"
            )
    })
    public ResponseEntity<AppointmentResponseDto> getAppointmentById(
            @Parameter(description = "Unique identifier of the appointment", required = true, example = "1")
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(service.getAppointmentById(id));
    }

    @PatchMapping("/{id}/status")
    @Operation(
            summary = "Update appointment status",
            description = "Updates the status of a specific appointment"
    )
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "200",
                    description = "Appointment status updated successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AppointmentResponseDto.class))
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "400",
                    description = "Invalid status value"
            ),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(
                    responseCode = "404",
                    description = "Appointment not found"
            )
    })
    public ResponseEntity<AppointmentResponseDto> updateStatus(
            @Parameter(description = "Unique identifier of the appointment", required = true, example = "1")
            @PathVariable Long id,
            @Parameter(description = "New appointment status", required = true, example = "CONFIRMED")
            @RequestParam AppointmentStatus status
    ) {
        return ResponseEntity.ok(service.updateStatus(id, status));
    }
}

