package fpt.edu.vn.gms.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@CrossOrigin(origins = "http://localhost:5173")

public class AppointmentRequestDto {

    @NotBlank(message = "Tên khách hàng không được để trống")
    private String customerName;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(
            regexp = "^(0|\\+84)(\\d{9})$",
            message = "Số điện thoại không hợp lệ, phải có 10 số và bắt đầu bằng 0 hoặc +84"
    )
    private String phoneNumber;

    @NotBlank(message = "Biển số xe không được để trống")
    @Size(max = 20, message = "Biển số xe không được vượt quá 20 ký tự")
    private String licensePlate;

    @NotNull(message = "Ngày hẹn không được để trống")
    @FutureOrPresent(message = "Ngày hẹn phải là hiện tại hoặc tương lai")
    private LocalDate appointmentDate;

    @NotNull(message = "Khung giờ không được để trống")
    @Min(value = 0, message = "Khung giờ không hợp lệ")
    private Integer timeSlotIndex;

    @NotEmpty(message = "Danh sách loại dịch vụ không được để trống")
    private List<@NotNull(message = "ID loại dịch vụ không được null") Long> serviceType;

    @Size(max = 255, message = "Ghi chú không được vượt quá 255 ký tự")
    private String note;

    private boolean isActive;
}
