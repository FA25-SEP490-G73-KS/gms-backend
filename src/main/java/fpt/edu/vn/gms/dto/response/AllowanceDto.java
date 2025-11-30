package fpt.edu.vn.gms.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
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

    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDateTime createdAt;

    private String createdBy;
}
