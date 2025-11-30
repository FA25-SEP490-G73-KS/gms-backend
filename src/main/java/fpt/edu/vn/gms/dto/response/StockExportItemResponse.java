package fpt.edu.vn.gms.dto.response;

import fpt.edu.vn.gms.common.enums.ExportStatus;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StockExportItemResponse {

    private Long itemId;
    private String sku;
    private double quantity;
    private double quantityInStock;
    private double exportedQuantity;
    private ExportStatus exportStatus;
}

