package fpt.edu.vn.gms.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class StockExportResponseDto {

    private Long exportId;
    private String exportCode;
    private LocalDateTime createdAt;
}
