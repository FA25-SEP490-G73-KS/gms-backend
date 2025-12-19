
package fpt.edu.vn.gms.service.impl;

import lombok.extern.slf4j.Slf4j;

import fpt.edu.vn.gms.dto.BrandDto;
import fpt.edu.vn.gms.dto.VehicleInfoDto;
import fpt.edu.vn.gms.dto.VehicleModelDto;
import fpt.edu.vn.gms.dto.response.LicensePlateCheckResponseDto;
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
@Slf4j
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
                .brandId(vehicle.getVehicleModel().getBrand().getBrandId())
                .brandName(vehicle.getVehicleModel().getBrand().getName())
                .modelId(vehicle.getVehicleModel().getVehicleModelId())
                .modelName(vehicle.getVehicleModel().getName())
                .vin(vehicle.getVin())
                .year(vehicle.getYear())
                .build();
    }

    @Override
    public boolean existsByLicensePlate(String licensePlate) {
        return vehicleRep.findByLicensePlate(licensePlate).isPresent();
    }

    @Override
    public boolean isLicensePlateOwnedByCustomer(String licensePlate, Long customerId) {
        return vehicleRep.findByLicensePlateAndCustomer_CustomerId(licensePlate, customerId).isPresent();
    }

    @Override
    public LicensePlateCheckResponseDto checkLicensePlateAndCustomer(String licensePlate, Long customerId) {
        log.info("Kiểm tra biển số xe [{}] cho customerId [{}]", licensePlate, customerId);
        var vehicleOpt = vehicleRep.findByLicensePlate(licensePlate);
        if (vehicleOpt.isEmpty()) {
            log.info("Biển số [{}] chưa tồn tại trong hệ thống", licensePlate);
            return LicensePlateCheckResponseDto.builder()
                    .isExists(false)
                    .isSameCustomer(false)
                    .licensePlate(licensePlate)
                    .customerName(null)
                    .customerPhone(null)
                    .build();
        }
        var vehicle = vehicleOpt.get();
        boolean isSameCustomer = vehicle.getCustomer() != null && vehicle.getCustomer().getCustomerId().equals(customerId);
        log.info("Biển số [{}] đã tồn tại. isSameCustomer: {}. Chủ xe: {} - {}", licensePlate, isSameCustomer, vehicle.getCustomer() != null ? vehicle.getCustomer().getFullName() : null, vehicle.getCustomer() != null ? vehicle.getCustomer().getPhone() : null);
        return LicensePlateCheckResponseDto.builder()
                .isExists(true)
                .isSameCustomer(isSameCustomer)
                .licensePlate(vehicle.getLicensePlate())
                .customerName(vehicle.getCustomer() != null ? vehicle.getCustomer().getFullName() : null)
                .customerPhone(vehicle.getCustomer() != null ? vehicle.getCustomer().getPhone() : null)
                .build();
    }

}
