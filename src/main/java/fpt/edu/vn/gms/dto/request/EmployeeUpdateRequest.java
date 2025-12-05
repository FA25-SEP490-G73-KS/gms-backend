package fpt.edu.vn.gms.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class EmployeeUpdateRequest {

    @NotBlank(message = "Họ tên không được để trống")
    private String fullName;

    @NotBlank(message = "Số điện thoại không được để trống")
    private String phone;

    @NotBlank(message = "Tỉnh/Thành phố không được để trống")
    private String province;

    @NotBlank(message = "Phường/Xã không được để trống")
    private String ward;

    @NotBlank(message = "Địa chỉ chi tiết không được để trống")
    private String addressDetail;

    @NotBlank(message = "Vị trí không được để trống")
    private String position; // string, map sang enum Role

    @NotNull(message = "Lương ngày không được để trống")
    private BigDecimal dailySalary;

    @NotNull(message = "Ngày bắt đầu không được để trống")
    private LocalDateTime hireDate;

    private LocalDateTime terminationDate;

    @NotBlank(message = "Trạng thái không được để trống")
    private String status;
}

