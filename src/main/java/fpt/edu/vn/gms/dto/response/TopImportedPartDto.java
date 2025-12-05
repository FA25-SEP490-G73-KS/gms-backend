package fpt.edu.vn.gms.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopImportedPartDto {

    private Long partId;
    private String partName;
    private String unitName;
    private Double totalImportedQuantity;
}

