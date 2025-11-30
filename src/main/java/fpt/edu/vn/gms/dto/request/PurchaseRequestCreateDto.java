package fpt.edu.vn.gms.dto.request;

import fpt.edu.vn.gms.dto.PartItemDto;
import lombok.Data;

import java.util.List;

@Data
public class PurchaseRequestCreateDto {

    private Long createdById;

    private String reason;
    private String note;

    private List<PartItemDto> items;
}
