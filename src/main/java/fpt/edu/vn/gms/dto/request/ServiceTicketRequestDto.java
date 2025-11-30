package fpt.edu.vn.gms.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@Getter
@Schema(description = "Request tạo/cập nhật phiếu dịch vụ")
public class ServiceTicketRequestDto {

    @Schema(description = "Có phải là chủ sở hữu của xe?")
    private Boolean forceAssignVehicle;

    @Schema(description = "ID lịch hẹn (nullable nếu tạo mới)")
    private Long appointmentId;

    @Schema(description = "Danh sách ID loại dịch vụ", example = "[1, 2, 3]", required = true)
    private List<Long> serviceTypeIds;

    @Schema(description = "Thông tin khách hàng")
    private CustomerRequestDto customer;

    @Schema(description = "Thông tin xe")
    private VehicleRequestDto vehicle;

    @Schema(description = "Danh sách ID kỹ thuật viên được giao", example = "[2, 3]")
    private List<Long> assignedTechnicianIds;

    @Schema(description = "Tình trạng tiếp nhận xe", example = "Xe có vết xước bên trái")
    private String receiveCondition;

    @Schema(description = "Ngày giao xe dự kiến", example = "2025-11-16")
    private LocalDate expectedDeliveryAt;
}
