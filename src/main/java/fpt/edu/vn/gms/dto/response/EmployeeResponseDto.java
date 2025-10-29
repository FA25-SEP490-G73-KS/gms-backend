package fpt.edu.vn.gms.dto.response;

import fpt.edu.vn.gms.common.Position;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class EmployeeResponseDto {

    private Long employeeId;

    // --- Thông tin cá nhân ---
    private String fullName;
    private String gender;          // Nam, Nữ, Khác
    private LocalDate dateOfBirth;
    private String phone;
    private String email;
    private String address;

    // --- Thông tin công việc ---
    private Position position;      // TECHNICIAN, MANAGER, ...
    private LocalDateTime hireDate;
    private String status;          // Active, Nghỉ việc, Tạm ngưng

    // --- Thông tin account ---
    private String username;        // null nếu kỹ thuật viên
    private String role;            // null nếu kỹ thuật viên
}