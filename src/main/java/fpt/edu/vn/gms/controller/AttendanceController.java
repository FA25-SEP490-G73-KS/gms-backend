package fpt.edu.vn.gms.controller;

import fpt.edu.vn.gms.common.annotations.AllowRoles;
import fpt.edu.vn.gms.common.annotations.CurrentUser;
import fpt.edu.vn.gms.common.enums.Role;
import fpt.edu.vn.gms.dto.request.AttendanceRequestDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import fpt.edu.vn.gms.dto.response.ApiResponse;
import fpt.edu.vn.gms.dto.response.AttendanceResponseDTO;
import fpt.edu.vn.gms.dto.response.AttendanceSummaryDTO;
import fpt.edu.vn.gms.entity.Employee;
import fpt.edu.vn.gms.service.AttendanceService;
import fpt.edu.vn.gms.utils.AppRoutes;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping(path = AppRoutes.ATTENDANCES_PREFIX, produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AttendanceController {
  AttendanceService attendanceService;

  // Lưu điểm danh hàng loạt
  @PostMapping("/mark")
  @Operation(
          summary = "Điểm danh hàng loạt cho nhân viên",
          description = "MANAGER thực hiện điểm danh trong ngày, trả về danh sách điểm danh mới nhất."
  )
  @ApiResponses(value = {
          @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Điểm danh thành công")
  })
  public ResponseEntity<ApiResponse<List<AttendanceResponseDTO>>> markAttendances(
          @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
          @RequestBody @Valid List<AttendanceRequestDTO> requests,
          @CurrentUser Employee manager
  ) {

    LocalDate targetDate = (date == null ? LocalDate.now() : date);

    attendanceService.markAttendances(targetDate, requests, manager.getEmployeeId());

    // Trả về danh sách điểm danh mới nhất
    List<AttendanceResponseDTO> updatedList = attendanceService.getAttendancesByDate(targetDate);

    return ResponseEntity.ok(
            ApiResponse.success(
                    "Điểm danh thành công",
                    updatedList
            )
    );
  }


  // Lấy danh sách điểm danh theo ngày
  @Operation(summary = "Lấy danh sách điểm danh theo ngày", description = "MANAGER lấy danh sách trạng thái điểm danh của tất cả nhân viên trong một ngày cụ thể.")
  @ApiResponses(value = {
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lấy danh sách thành công")
  })
  @GetMapping("/daily")
  public ResponseEntity<ApiResponse<List<AttendanceResponseDTO>>> getAttendancesByDate(
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
    return ResponseEntity.ok(ApiResponse.success("Lấy danh sách trạng thái điểm danh thành công",
        attendanceService.getAttendancesByDate(date)));
  }

  // Lấy trạng thái điểm danh theo nhiều ngày
  @Operation(summary = "Theo dõi trạng thái điểm danh nhiều ngày", description = "MANAGER theo dõi trạng thái điểm danh của nhân viên trong 7, 14,... ngày liên tiếp.")
  @ApiResponses(value = {
      @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lấy dữ liệu thành công")
  })
  @GetMapping("/summary")
  public ResponseEntity<ApiResponse<List<AttendanceSummaryDTO>>> getAttendanceSummary(
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
    return ResponseEntity.ok(ApiResponse.success("Lấy danh sách trạng thái điểm danh thành công",
        attendanceService.getAttendanceSummary(startDate, endDate)));
  }
}
