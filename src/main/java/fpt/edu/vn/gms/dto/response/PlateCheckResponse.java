package fpt.edu.vn.gms.dto.response;

import fpt.edu.vn.gms.dto.VehicleInfoDto;
import fpt.edu.vn.gms.entity.Customer;
import fpt.edu.vn.gms.entity.Vehicle;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PlateCheckResponse {

    private String status;     // OK, NOT_FOUND, OWNED_BY_OTHER
    private VehicleInfoDto vehicle;
    private CustomerResponseDto owner;

    public static PlateCheckResponse status(String s) {
        return PlateCheckResponse.builder().status(s).build();
    }

    public static PlateCheckResponse ownerConflict(Customer owner) {
        return PlateCheckResponse.builder()
                .status("OWNED_BY_OTHER")
                .owner(new CustomerResponseDto(owner.getCustomerId(), owner.getFullName(), owner.getPhone()))
                .build();
    }

    public static PlateCheckResponse ok(Vehicle vehicle) {
        return PlateCheckResponse.builder()
                .status("OK")
                .vehicle(null)
                .build();
    }
}

