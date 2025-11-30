package fpt.edu.vn.gms.service.impl;

import fpt.edu.vn.gms.dto.request.PartUpdateReqDto;
import fpt.edu.vn.gms.dto.response.PartReqDto;
import fpt.edu.vn.gms.entity.*;
import fpt.edu.vn.gms.exception.ResourceNotFoundException;
import fpt.edu.vn.gms.mapper.PartMapper;
import fpt.edu.vn.gms.repository.*;
import fpt.edu.vn.gms.service.PartService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PartServiceImpl implements PartService {

    SkuGenerator skuGenerator;
    PartRepository partRepository;
    CategoryRepository categoryRepo;
    MarketRepository marketRepo;
    UnitRepository unitRepo;
    VehicleModelRepository vehicleModelRepo;
    SupplierRepository supplierRepo;
    PartMapper partMapper;

    @Override
    public Page<PartReqDto> getAllPart(int page, int size) {

        Pageable pageable = PageRequest.of(page, size);

        Page<Part> parts = partRepository.findAll(pageable);

        // map entity -> dto
        return parts.map(partMapper::toDto);
    }

    @Override
    public PartReqDto getPartById(Long id) {

        Part part = partRepository.findById(id).orElse(null);

        return partMapper.toDto(part);
    }

    @Override
    @Transactional
    public PartReqDto createPart(PartUpdateReqDto dto) {

        log.info("Creating new part with name={}", dto.getName());

        // --- Category ---
        PartCategory category = null;
        if (dto.getCategoryId() != null) {
            category = categoryRepo.findById(dto.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Không tìm thấy danh mục với ID: " + dto.getCategoryId()));
        }

        // --- Market ---
        Market market = marketRepo.findById(dto.getMarketId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thị trường!"));

        // --- Unit ---
        Unit unit = unitRepo.findById(dto.getUnitId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn vị tính!"));

        // --- Vehicle Model ---
        VehicleModel vehicleModel = vehicleModelRepo.findById(dto.getVehicleModelId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy mẫu xe!"));

        // --- Supplier ---
        Supplier supplier = supplierRepo.findById(dto.getSupplierId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nhà cung cấp " +  + dto.getSupplierId()));

        // --- Tính giá bán ---
        BigDecimal purchase = dto.getPurchasePrice();
        BigDecimal selling = purchase.multiply(BigDecimal.valueOf(1.10));

        // --- Build entity ---
        Part part = Part.builder()
                .name(dto.getName())
                .category(category)
                .vehicleModel(vehicleModel)
                .market(market)
                .purchasePrice(purchase)
                .sellingPrice(selling)
                .discountRate(BigDecimal.valueOf(10.0))
                .supplier(supplier)
                .unit(unit)
                .isUniversal(dto.isUniversal())
                .specialPart(dto.isSpecialPart())
                .build();

        part.setSku(skuGenerator.generateSku(part));

        Part saved = partRepository.save(part);

        log.info("Created part id={} name={}", saved.getPartId(), saved.getName());

        return partMapper.toDto(saved);
    }

    @Transactional
    @Override
    public PartReqDto updatePart(Long id, PartUpdateReqDto dto) {

        log.info("Updating part id={}", id);

        Part part = partRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy linh kiện ID: " + id));

        log.debug("Part BEFORE update: {}", part);

        if (dto.getName() != null) {
            part.setName(dto.getName());
        }

        if (dto.getNote() != null) {
            part.setNote(dto.getNote());
        }

        if (dto.getReorderLevel() != null) {
            part.setReorderLevel(dto.getReorderLevel());
        }

        if (dto.getCategoryId() != null) {
            PartCategory category = categoryRepo.findById(dto.getCategoryId())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục"));
            part.setCategory(category);
        }

        if (dto.getMarketId() != null) {
            Market market = marketRepo.findById(dto.getMarketId())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thị trường"));
            part.setMarket(market);
        }

        if (dto.getUnitId() != null) {
            Unit unit = unitRepo.findById(dto.getUnitId())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn vị tính"));
            part.setUnit(unit);
        }

        if (dto.getVehicleModelId() != null) {
            VehicleModel model = vehicleModelRepo.findById(dto.getVehicleModelId())
                    .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy mẫu xe"));
            part.setVehicleModel(model);
        }

        if (dto.getPurchasePrice() != null) {
            BigDecimal purchase = dto.getPurchasePrice();
            BigDecimal selling = purchase.multiply(BigDecimal.valueOf(1.10)); // auto tính giá bán
            part.setPurchasePrice(purchase);
            part.setSellingPrice(selling);
        }

        if (dto.getSellingPrice() != null) {
            part.setSellingPrice(dto.getSellingPrice()); // nếu muốn override giá bán
        }

        if (dto.getSupplierId() != null) {
            Supplier supplier = supplierRepo.findById(dto.getSupplierId())
                            .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nhà cung cấp"));
            part.setSupplier(supplier);
        }

        part.setUniversal(dto.isUniversal());
        part.setSpecialPart(dto.isSpecialPart());

        part.setSku(skuGenerator.generateSku(part));

        Part saved = partRepository.save(part);

        log.info("Updated part id={} successfully", id);
        log.debug("Part AFTER update: {}", saved);

        return partMapper.toDto(saved);
    }


    @Override
    public Page<PartReqDto> getPartByCategory(String categoryName, int page, int size) {

        Pageable pageable = PageRequest.of(page, size);

        Page<Part> parts = partRepository.findByCategory(categoryName, pageable);

        // Dùng Page.map để giữ thông tin phân trang
        return parts.map(partMapper::toDto);
    }
}
