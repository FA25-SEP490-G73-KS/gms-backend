package fpt.edu.vn.gms.dto.request;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class TicketUpdateReqDto {

    private LocalDate deliveryAt;
    // --- TECHNICIAN ---
    private List<Long> assignedTechnicianId;

    // --- SERVICE TYPE ---
    private List<Long> serviceTypeIds;
}
