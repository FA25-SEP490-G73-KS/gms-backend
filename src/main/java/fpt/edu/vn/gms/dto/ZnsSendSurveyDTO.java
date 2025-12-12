package fpt.edu.vn.gms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ZnsSendSurveyDTO {
    private String customerName;
    private String carModel;
    private String licensePlate;
    private Long serviceId;
    private String serviceCode;
}
