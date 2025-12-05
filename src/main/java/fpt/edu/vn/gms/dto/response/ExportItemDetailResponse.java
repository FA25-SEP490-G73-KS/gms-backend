package fpt.edu.vn.gms.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ExportItemDetailResponse {

    private Long id;
    private String sku;
    private String name;
    private Double required;
    private Double exported;
    private Double remaining;
    private String status;
    private List<ExportItemHistoryDto> history;

    @Data
    @Builder
    public static class ExportItemHistoryDto {
        private Long id;
        private Double quantity;
        private LocalDateTime exportedAt;
        private Long exportedById;
        private String exportedByName;
    }
}
