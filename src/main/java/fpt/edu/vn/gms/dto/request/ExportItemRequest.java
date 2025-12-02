package fpt.edu.vn.gms.dto.request;

import lombok.Data;

@Data
public class ExportItemRequest {

    private Double quantity;
    private Long receiverId;
    private String note;
}

