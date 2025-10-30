package fpt.edu.vn.gms.service;

import fpt.edu.vn.gms.dto.BrandDto;
import fpt.edu.vn.gms.dto.VehicleModelDto;

import java.util.List;

public interface VehicleService {

    List<BrandDto> getAllBrands();
    List<VehicleModelDto> getModelsByBrand(Long brandId);
}
