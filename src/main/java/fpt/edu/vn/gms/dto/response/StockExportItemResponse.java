package fpt.edu.vn.gms.dto.response;

import fpt.edu.vn.gms.common.enums.ExportStatus;
import fpt.edu.vn.gms.entity.StockExportItem;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class StockExportItemResponse {

    private Long itemId;

    private PartReqDto part;

    private double quantity;
    private double exportedQuantity;
    private ExportStatus exportStatus;

    private List<StockExportItem> stockExportItems;
}

