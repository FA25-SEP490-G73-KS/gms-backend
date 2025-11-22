package fpt.edu.vn.gms.dto.request;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class TicketUpdateReqDto {

    // --- CUSTOMER ---
    private String customerName;
    private String customerPhone;

    // --- VEHICLE ---
    private Long brandId;
    private String brandName;

    private Long modelId;
    private String modelName;

    private Long vehicleId;
    private String licensePlate;
    private String vin;

    // --- TECHNICIAN ---
    private Long assignedTechnicianId;

    // --- SERVICE TYPE ---
    private List<Long> serviceTypeIds;
}
