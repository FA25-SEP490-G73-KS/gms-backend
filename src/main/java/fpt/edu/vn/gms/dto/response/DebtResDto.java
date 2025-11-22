package fpt.edu.vn.gms.dto.response;

import fpt.edu.vn.gms.common.enums.DebtStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DebtResDto {

    @Schema(description = "ID công nợ")
    private Long id;

    @Schema(description = "Khách hàng")
    private Long customerId;

    @Schema(description = "Phiếu dịch vụ liên quan")
    private Long serviceTicketId;

    @Schema(description = "Mã phiếu dịch vụ liên quan")
    private String serviceTicketCode;

    @Schema(description = "Số tiền công nợ")
    private BigDecimal amount;

    @Schema(description = "Số tiền đã thanh toán cho công nợ này (mới tạo thường = 0)")
    private BigDecimal paidAmount;

    @Schema(description = "Trạng thái công nợ (CÒN_NỢ / ĐÃ_TẤT_TOÁN / ...)")
    private String statusLabel;

    @Schema(description = "Ngày hẹn trả")
    private LocalDate dueDate;

    @Schema(description = "Thời điểm tạo")
    private LocalDateTime createdAt;
}
