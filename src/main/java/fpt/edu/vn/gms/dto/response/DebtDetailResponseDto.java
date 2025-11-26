package fpt.edu.vn.gms.dto.response;

import fpt.edu.vn.gms.dto.TransactionResponseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DebtDetailResponseDto {

    @Schema(description = "ID công nợ")
    private Long id;

    @Schema(description = "Khách hàng")
    private Long customerFullName;

    @Schema(description = "Phiếu dịch vụ liên quan")
    private ServiceTicketResponseDto serviceTicket;

    @Schema(description = "Số tiền công nợ")
    private BigDecimal amount;

    @Schema(description = "Số tiền đã thanh toán cho công nợ này (mới tạo thường = 0)")
    private BigDecimal paidAmount;

    @Schema(description = "Trạng thái công nợ (CÒN_NỢ / ĐÃ_TẤT_TOÁN / ...)")
    private String status;

    @Schema(description = "Ngày hẹn trả")
    private LocalDate dueDate;

    @Schema(description = "Thời điểm tạo")
    private LocalDateTime createdAt;

    private List<TransactionResponseDto> transactions;

}
