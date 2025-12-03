package fpt.edu.vn.gms.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ManualTransactionRequest {

    @Schema(description = "EXPORT | RECEIPT", example = "EXPORT")
    private String type;

    @Schema(example = "false")
    private Boolean isDraft;

    private String reason;
    private String createdBy;
    private Long receiverId;
    private Long supplierId;
    private String note;
    private List<ManualTransactionItemRequest> items;
}

