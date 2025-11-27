package fpt.edu.vn.gms.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class ManualVoucherResponseDto {

    private Long id;
    private String code;
    private String type; // NCC / LUONG / KHAC

    private BigDecimal amount;
    private String category;
    private String description;

    private LocalDateTime createdAt;
    private String createdBy;

    private String status; // COMPLETED / ...
    private String attachmentUrl;
}
