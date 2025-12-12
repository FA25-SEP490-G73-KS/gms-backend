package fpt.edu.vn.gms.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class CreateStockExportRequest {

    private Long quotationId;
    private String reason;
    private List<Long> quotationItemIds;
}
