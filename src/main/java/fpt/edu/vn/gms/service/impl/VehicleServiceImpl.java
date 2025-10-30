package fpt.edu.vn.gms.service.impl;

import fpt.edu.vn.gms.dto.BrandDto;
import fpt.edu.vn.gms.dto.VehicleModelDto;
import fpt.edu.vn.gms.repository.BrandRepository;
import fpt.edu.vn.gms.repository.VehicleModelRepository;
import fpt.edu.vn.gms.service.VehicleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VehicleServiceImpl implements VehicleService {

    private final VehicleModelRepository vehicleModelRepository;
    private final BrandRepository brandRepository;

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
}
