package fpt.edu.vn.gms.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateDebtFromPaymentReq {

    @Schema(description = "Ngày hẹn trả công nợ", example = "2025-11-30")
    @NotNull(message = "Ngày hẹn trả không được để trống")
    private LocalDate dueDate;
}
