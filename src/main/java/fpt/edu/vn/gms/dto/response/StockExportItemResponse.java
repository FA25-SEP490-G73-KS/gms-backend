package fpt.edu.vn.gms.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StockExportItemResponse {

    private Long id;
    private String sku;
    private String name;
    private Double quantityRequired;
    private Double quantityExported;
    private String status;
}
