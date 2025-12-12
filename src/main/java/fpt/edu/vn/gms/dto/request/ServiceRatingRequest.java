package fpt.edu.vn.gms.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ServiceRatingRequest {

    @NotNull
    @Schema(description = "ID phiếu dịch vụ", example = "123")
    private Long serviceTicketId;

    @NotNull
    @Min(1)
    @Max(5)
    @Schema(description = "Số sao đánh giá (1-5)", example = "4")
    private Integer stars;

    @Schema(description = "Nhận xét của khách hàng", example = "Nhân viên thân thiện, sửa nhanh.")
    private String feedback;
}
