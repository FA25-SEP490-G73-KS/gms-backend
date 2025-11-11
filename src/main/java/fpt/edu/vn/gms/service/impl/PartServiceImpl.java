package fpt.edu.vn.gms.service.impl;

import fpt.edu.vn.gms.dto.request.PartReqDto;
import fpt.edu.vn.gms.dto.response.PartResDto;
import fpt.edu.vn.gms.entity.Part;
import fpt.edu.vn.gms.entity.PartCategory;
import fpt.edu.vn.gms.entity.VehicleModel;
import fpt.edu.vn.gms.exception.ResourceNotFoundException;
import fpt.edu.vn.gms.mapper.PartMapper;
import fpt.edu.vn.gms.repository.CategoryRepository;
import fpt.edu.vn.gms.repository.PartRepository;
import fpt.edu.vn.gms.repository.VehicleModelRepository;
import fpt.edu.vn.gms.service.PartService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PartServiceImpl implements PartService {

    private final PartRepository partRepository;
    private final VehicleModelRepository modelRepo;
    private final CategoryRepository categoryRepo;
    private final PartMapper partMapper;

    @Override
    public Page<PartResDto> getAllPart(int page, int size) {

        Pageable pageable = PageRequest.of(page, size);

        Page<Part> parts = partRepository.findAll(pageable);

        // map entity -> dto
        return parts.map(partMapper::toDto);
    }

    @Override
    public PartResDto createPart(PartReqDto dto) {

        // --- Kiểm tra danh mục (nếu có) ---
        PartCategory category = null;
        if (dto.getCategoryId() != null) {
            category = categoryRepo.findById(dto.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục linh kiện ID: " + dto.getCategoryId()));
        }

        // --- Lấy các model xe tương thích (nếu có) ---
        Set<VehicleModel> compatibleModels = new HashSet<>();
        if (dto.getCompatibleVehicleModelIds() != null && !dto.getCompatibleVehicleModelIds().isEmpty()) {
            compatibleModels.addAll(modelRepo.findAllById(dto.getCompatibleVehicleModelIds()));
        }

        // --- Tạo đối tượng Part ---
        Part newPart = Part.builder()
                .name(dto.getName())
                .category(category)
                .compatibleVehicles(compatibleModels)
                .market(dto.getMarket())
                .isUniversal(dto.isUniversal())
                .purchasePrice(dto.getPurchasePrice())
                .sellingPrice(dto.getSellingPrice())
                .discountRate(dto.getDiscountRate())
                .unit(dto.getUnit())
                .reorderLevel(dto.getReorderLevel() != null ? dto.getReorderLevel() : 0.0)
                .quantityInStock(0.0) // chưa nhập, để 0
                .reservedQuantity(0.0) // chưa giữ, để 0
                .specialPart(dto.isSpecialPart()) // vì là linh kiện unknown
                .build();

        Part saved = partRepository.save(newPart);

        return partMapper.toDto(saved);
    }

    @Override
    public Page<PartResDto> getPartByCategory(String categoryName, int page, int size) {

        Pageable pageable = PageRequest.of(page, size);

        Page<Part> parts = partRepository.findByCategory(categoryName, pageable);

        // Dùng Page.map để giữ thông tin phân trang
        return parts.map(partMapper::toDto);
    }
}
