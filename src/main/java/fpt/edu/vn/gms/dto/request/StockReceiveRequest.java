package fpt.edu.vn.gms.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StockReceiveRequest {

    @Schema(example = "Đã nhập kho xong")
    private String note;

    @NotNull
    @Min(0)
    @Schema(example = "1")
    private Double quantityReceived;
}
