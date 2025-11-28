package fpt.edu.vn.gms.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@Data
@AllArgsConstructor
public class AllowanceDto {

    private String type;
    private BigDecimal amount;
    private LocalDateTime createdAt;
    private String createdBy;
}
