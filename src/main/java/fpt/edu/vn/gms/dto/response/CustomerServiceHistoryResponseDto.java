package fpt.edu.vn.gms.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CustomerServiceHistoryResponseDto {
    private String fullName;
    private String phone;
    private List<VehicleServiceInfo> vehicles;

    @Data
    @Builder
    public static class VehicleServiceInfo {
        private String licensePlate;
        private String modelName;
        private String brandName;
        private String lastServiceDate; // ISO string
    }
}

