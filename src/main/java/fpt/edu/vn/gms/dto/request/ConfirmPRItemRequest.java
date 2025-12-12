package fpt.edu.vn.gms.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ConfirmPRItemRequest {

    private boolean approved;
    private String note;
}
