package fpt.edu.vn.gms.service;

import fpt.edu.vn.gms.dto.BrandDto;
import fpt.edu.vn.gms.dto.VehicleInfoDto;
import fpt.edu.vn.gms.dto.VehicleModelDto;
import fpt.edu.vn.gms.dto.response.LicensePlateCheckResponseDto;

import java.util.List;

public interface VehicleService {
    List<BrandDto> getAllBrands();

    List<VehicleModelDto> getModelsByBrand(Long brandId);

    VehicleInfoDto findByLicensePlate(String licensePlate);

    boolean existsByLicensePlate(String licensePlate);

    boolean isLicensePlateOwnedByCustomer(String licensePlate, Long customerId);

    LicensePlateCheckResponseDto checkLicensePlateAndCustomer(String licensePlate, Long customerId);
}
