package fpt.edu.vn.gms.service.impl;

import fpt.edu.vn.gms.dto.request.PartUpdateReqDto;
import fpt.edu.vn.gms.dto.response.PartReqDto;
import fpt.edu.vn.gms.dto.request.PartResDto;
import fpt.edu.vn.gms.entity.*;
import fpt.edu.vn.gms.exception.ResourceNotFoundException;
import fpt.edu.vn.gms.mapper.PartMapper;
import fpt.edu.vn.gms.repository.*;
import fpt.edu.vn.gms.service.PartService;
import lombok.RequiredArgsConstructor;
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
public class PartServiceImpl implements PartService {

    private final PartRepository partRepository;
    private final CategoryRepository categoryRepo;
    private final MarketRepository marketRepo;
    private final UnitRepository unitRepo;
    private final VehicleModelRepository vehicleModelRepo;
    private final PartMapper partMapper;

    @Override
    public Page<PartReqDto> getAllPart(int page, int size) {

        Pageable pageable = PageRequest.of(page, size);

        Page<Part> parts = partRepository.findAll(pageable);

        // map entity -> dto
        return parts.map(partMapper::toDto);
    }

    @Override
    @Transactional
    public PartReqDto createPart(PartResDto dto) {

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
        VehicleModel vehicleModel = vehicleModelRepo.findById(dto.getVehicleModel())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy mẫu xe!"));

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
                .quantityInStock(dto.getQuantity())
                .discountRate(BigDecimal.valueOf(10.0))
                .unit(unit)
                .isUniversal(dto.isUniversal())
                .specialPart(dto.isSpecialPart())
                .note(dto.getNote())
                .build();

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

        log.debug("Part before update: {}", part);

        // Category
        PartCategory category = categoryRepo.findById(dto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy danh mục"));

        // Market
        Market market = marketRepo.findById(dto.getMarketId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thị trường"));

        // Unit
        Unit unit = unitRepo.findById(dto.getUnitId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn vị tính"));

        // Vehicle Model
        VehicleModel model = vehicleModelRepo.findById(dto.getVehicleModelId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy mẫu xe"));

        // Update data
        part.setName(dto.getName());
        part.setCategory(category);
        part.setMarket(market);
        part.setVehicleModel(model);
        part.setUnit(unit);

        part.setPurchasePrice(dto.getPurchasePrice());
        part.setSellingPrice(dto.getSellingPrice());   // FE cho nhập giá bán
        part.setUniversal(dto.isUniversal());
        part.setSpecialPart(dto.isSpecialPart());
        part.setNote(dto.getNote());

        Part saved = partRepository.save(part);

        log.info("Updated part id={} successfully", id);
        log.debug("Part after update: {}", saved);

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
