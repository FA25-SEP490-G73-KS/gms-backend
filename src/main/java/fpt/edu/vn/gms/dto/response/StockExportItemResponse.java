package fpt.edu.vn.gms.dto.response;

import fpt.edu.vn.gms.common.ExportStatus;

public class StockExportItemResponse {

    private Long itemId;
    private String itemName;
    private double quantity;
    private double quantityInStock;
    private double exportedQuantity;
    private ExportStatus exportStatus;
}
