package fpt.edu.vn.gms.controller;

import fpt.edu.vn.gms.common.annotations.CurrentUser;
import fpt.edu.vn.gms.dto.response.ApiResponse;
import fpt.edu.vn.gms.dto.response.NotificationResponseDto;
import fpt.edu.vn.gms.entity.Employee;
import fpt.edu.vn.gms.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static fpt.edu.vn.gms.utils.AppRoutes.NOTIFICATIONS_PREFIX;

@Tag(name = "notifications", description = "Quản lý thông báo cho người dùng")
@CrossOrigin(origins = "${fe-local-host}")
@RestController
@RequestMapping(path = NOTIFICATIONS_PREFIX, produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    @Operation(summary = "Lấy thông báo của người dùng", description = "Lấy danh sách các thông báo cho người dùng đã được xác thực.")
    @ApiResponses(value = {
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lấy thông báo thành công"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Chưa xác thực", content = @Content(schema = @Schema(hidden = true))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "500", description = "Lỗi máy chủ nội bộ", content = @Content(schema = @Schema(hidden = true)))
    })
    public ResponseEntity<ApiResponse<List<NotificationResponseDto>>> getUserNotifications(
            @CurrentUser Employee employee) {
        List<NotificationResponseDto> notifications = notificationService.getNotificationsForUser(employee.getPhone());
        return ResponseEntity.status(200)
                .body(ApiResponse.success("Successfully", notifications));
    }

    // Lấy chi tiết 1 notification theo id
    @GetMapping("/{id}")
    @Operation(summary = "Lấy chi tiết thông báo", description = "Lấy chi tiết một thông báo theo id cho user hiện tại")
    public ResponseEntity<ApiResponse<NotificationResponseDto>> getNotificationById(
            @PathVariable Long id,
            @CurrentUser Employee employee) {
        NotificationResponseDto dto = notificationService.getNotificationById(id, employee.getPhone());
        return ResponseEntity.ok(ApiResponse.success("Successfully", dto));
    }

    @PatchMapping("/{id}/read")
    @Operation(summary = "Đánh dấu thông báo đã đọc", description = "Cập nhật trạng thái thông báo từ UNREAD sang READ")
    public ResponseEntity<ApiResponse<Void>> markNotificationAsRead(
            @PathVariable Long id,
            @CurrentUser Employee employee) {
        notificationService.markAsRead(id, employee.getPhone());
        return ResponseEntity.ok(ApiResponse.success("Updated", null));
    }
}
