package fpt.edu.vn.gms.service.impl;

import fpt.edu.vn.gms.dto.BrandDto;
import fpt.edu.vn.gms.dto.VehicleInfoDto;
import fpt.edu.vn.gms.dto.VehicleModelDto;
import fpt.edu.vn.gms.entity.Vehicle;
import fpt.edu.vn.gms.repository.BrandRepository;
import fpt.edu.vn.gms.repository.VehicleModelRepository;
import fpt.edu.vn.gms.repository.VehicleRepository;
import fpt.edu.vn.gms.service.VehicleService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class VehicleServiceImpl implements VehicleService {

    VehicleRepository vehicleRep;
    VehicleModelRepository vehicleModelRepository;
    BrandRepository brandRepository;

    public List<BrandDto> getAllBrands() {
        return brandRepository.findAll()
                .stream()
                .map(b -> new BrandDto(b.getBrandId(), b.getName()))
                .toList();
    }

    public List<VehicleModelDto> getModelsByBrand(Long brandId) {
        return vehicleModelRepository.findByBrandBrandId(brandId)
                .stream()
                .map(m -> new VehicleModelDto(m.getVehicleModelId(), m.getName()))
                .toList();
    }

    @Override
    public VehicleInfoDto findByLicensePlate(String licensePlate) {

        Vehicle vehicle = vehicleRep.findByLicensePlate(licensePlate)
                .orElseThrow(() -> new IllegalArgumentException("Không có biển số xe này!!!"));

        return VehicleInfoDto.builder()
                .vehicleId(vehicle.getVehicleId())
                .licensePlate(vehicle.getLicensePlate())
                .brandName(vehicle.getVehicleModel().getBrand().getName())
                .modelName(vehicle.getVehicleModel().getName())
                .vin(vehicle.getVin())
                .year(vehicle.getYear())
                .build();
    }


}
