package fpt.edu.vn.gms.dto.request;

import fpt.edu.vn.gms.dto.PartItemDto;
import lombok.Data;

import java.util.List;

@Data
public class StockExportCreateDto {

    private Long createdById;
    private Long receiverId;

    private Long damagedById;

    private String reason;
    private String note;

    private List<PartItemDto> items;
}


