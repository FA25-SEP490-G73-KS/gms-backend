package fpt.edu.vn.gms.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StockExportItemResponse {

    private Long id;
    private String sku;
    private String name;
    private Double quantityRequired;
    private Double quantityExported;
    private String status;
}
