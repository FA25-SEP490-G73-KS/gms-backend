package fpt.edu.vn.gms.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class ManualVoucherListResponseDto {

    private Long id;
    private String code;
    private String type;
    private String targetName;
    private BigDecimal amount;
    private LocalDateTime createdAt;
    private String status;
}
