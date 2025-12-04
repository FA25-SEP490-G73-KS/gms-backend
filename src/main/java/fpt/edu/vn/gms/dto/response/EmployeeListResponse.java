package fpt.edu.vn.gms.dto.response;

import fpt.edu.vn.gms.common.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeListResponse {

    private Long employeeId;
    private String fullName;
    private String phone;
    private Role role;
    private LocalDateTime hireDate;
    private BigDecimal dailySalary;
    /**
     * Trạng thái hiển thị tính động theo Attendance hôm nay:
     * - "Đang hoạt động" / "Nghỉ phép" / "Nghỉ làm"
     */
    private String status;
}
