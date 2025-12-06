package fpt.edu.vn.gms.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ServiceRatingResponse {

    @Schema(description = "ID đánh giá", example = "1")
    private Long id;

    @Schema(description = "ID phiếu dịch vụ", example = "123")
    private Long serviceTicketId;

    @Schema(description = "ID khách hàng", example = "55")
    private Long customerId;

    @Schema(description = "Số sao đánh giá", example = "4")
    private Integer stars;

    @Schema(description = "Nhận xét của khách hàng")
    private String feedback;

    @Schema(description = "Thời điểm tạo đánh giá")
    private LocalDateTime createdAt;
}

