package fpt.edu.vn.gms.controller;

import fpt.edu.vn.gms.dto.BrandDto;
import fpt.edu.vn.gms.dto.VehicleModelDto;
import fpt.edu.vn.gms.service.VehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
}

