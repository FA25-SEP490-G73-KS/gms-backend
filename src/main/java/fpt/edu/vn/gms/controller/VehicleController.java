package fpt.edu.vn.gms.controller;

import fpt.edu.vn.gms.dto.BrandDto;
import fpt.edu.vn.gms.dto.VehicleInfoDto;
import fpt.edu.vn.gms.dto.VehicleModelDto;
import fpt.edu.vn.gms.dto.response.ApiResponse;
import fpt.edu.vn.gms.entity.Vehicle;
import fpt.edu.vn.gms.service.VehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "${fe-local-host}")
@RestController
@RequestMapping("/api/vehicles")
@RequiredArgsConstructor
public class VehicleController {

    private final VehicleService vehicleService;

    @GetMapping("/brands")
    public ResponseEntity<List<BrandDto>> getAllBrands() {
        return ResponseEntity.ok(vehicleService.getAllBrands());
    }

    @GetMapping("/brands/{brandId}/models")
    public ResponseEntity<List<VehicleModelDto>> getModelsByBrand(@PathVariable Long brandId) {
        return ResponseEntity.ok(vehicleService.getModelsByBrand(brandId));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<VehicleInfoDto>> getLicensePlate(
            @RequestParam String licensePlate
    ) {

        VehicleInfoDto dto = vehicleService.findByLicensePlate(licensePlate);

        return ResponseEntity.status(200)
                .body(ApiResponse.success("Thành công!!!", dto));
    }

}

