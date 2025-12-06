package fpt.edu.vn.gms.service.impl;

import fpt.edu.vn.gms.common.enums.StockLevelStatus;
import fpt.edu.vn.gms.dto.request.PartUpdateReqDto;
import fpt.edu.vn.gms.dto.response.PartReqDto;
import fpt.edu.vn.gms.entity.*;
import fpt.edu.vn.gms.exception.ResourceNotFoundException;
import fpt.edu.vn.gms.mapper.PartMapper;
import fpt.edu.vn.gms.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PartServiceImplTest {

    @Mock
    SkuGenerator skuGenerator;
    @Mock
    PartRepository partRepository;
    @Mock
    CategoryRepository categoryRepo;
    @Mock
    MarketRepository marketRepo;
    @Mock
    UnitRepository unitRepo;
    @Mock
    VehicleModelRepository vehicleModelRepo;
    @Mock
    SupplierRepository supplierRepo;
    @Mock
    PartMapper partMapper;

    @InjectMocks
    PartServiceImpl service;

    private Part part;

    @BeforeEach
    void setUp() {
        part = Part.builder()
                .partId(1L)
                .name("Oil 5W30")
                .purchasePrice(new BigDecimal("100000"))
                .build();
    }

    @Test
    void getAllPart_ShouldReturnPagedDtos() {
        Pageable pageable = PageRequest.of(0, 5);
        Page<Part> page = new PageImpl<>(List.of(part), pageable, 1);
        when(partRepository.findAll(pageable)).thenReturn(page);

        PartReqDto dto = PartReqDto.builder()
                .partId(1L)
                .name("Oil 5W30")
                .build();
        when(partMapper.toDto(part)).thenReturn(dto);

        Page<PartReqDto> result = service.getAllPart(0, 5);

        assertEquals(1, result.getTotalElements());
        assertSame(dto, result.getContent().get(0));
        verify(partRepository).findAll(pageable);
        verify(partMapper).toDto(part);
    }

    @Test
    void getPartById_ShouldReturnDto_WhenFound() {
        when(partRepository.findById(1L)).thenReturn(Optional.of(part));
        PartReqDto dto = PartReqDto.builder().partId(1L).name("Oil 5W30").build();
        when(partMapper.toDto(part)).thenReturn(dto);

        PartReqDto result = service.getPartById(1L);

        assertSame(dto, result);
        verify(partRepository).findById(1L);
    }

    @Test
    void getPartById_ShouldReturnNullDto_WhenNotFound() {
        when(partRepository.findById(1L)).thenReturn(Optional.empty());
        when(partMapper.toDto(null)).thenReturn(null);

        PartReqDto result = service.getPartById(1L);

        assertNull(result);
        verify(partRepository).findById(1L);
    }

    @Test
    void createPart_ShouldResolveAllDependenciesAndSave() {
        PartUpdateReqDto dto = PartUpdateReqDto.builder()
                .name("Oil 5W30")
                .categoryId(1L)
                .marketId(2L)
                .unitId(3L)
                .vehicleModelId(4L)
                .supplierId(5L)
                .purchasePrice(new BigDecimal("100000"))
                .universal(false)
                .specialPart(true)
                .build();

        PartCategory category = PartCategory.builder().id(1L).build();
        Market market = Market.builder().id(2L).build();
        Unit unit = Unit.builder().id(3L).build();
        VehicleModel model = VehicleModel.builder().vehicleModelId(4L).build();
        Supplier supplier = Supplier.builder().id(5L).build();

        when(categoryRepo.findById(1L)).thenReturn(Optional.of(category));
        when(marketRepo.findById(2L)).thenReturn(Optional.of(market));
        when(unitRepo.findById(3L)).thenReturn(Optional.of(unit));
        when(vehicleModelRepo.findById(4L)).thenReturn(Optional.of(model));
        when(supplierRepo.findById(5L)).thenReturn(Optional.of(supplier));

        when(skuGenerator.generateSku(any(Part.class))).thenReturn("SKU123");

        Part saved = Part.builder()
                .partId(10L)
                .name("Oil 5W30")
                .purchasePrice(new BigDecimal("100000"))
                .sellingPrice(new BigDecimal("110000"))
                .sku("SKU123")
                .build();
        when(partRepository.save(any(Part.class))).thenReturn(saved);

        PartReqDto dtoResult = PartReqDto.builder()
                .partId(10L)
                .name("Oil 5W30")
                .build();
        when(partMapper.toDto(saved)).thenReturn(dtoResult);

        PartReqDto result = service.createPart(dto);

        assertSame(dtoResult, result);
        verify(categoryRepo).findById(1L);
        verify(marketRepo).findById(2L);
        verify(unitRepo).findById(3L);
        verify(vehicleModelRepo).findById(4L);
        verify(supplierRepo).findById(5L);
        verify(partRepository).save(any(Part.class));
        verify(partMapper).toDto(saved);
    }

    @Test
    void createPart_ShouldAllowNullCategoryAndUniversalVehicle() {
        PartUpdateReqDto dto = PartUpdateReqDto.builder()
                .name("Oil 5W30")
                .categoryId(null)
                .marketId(2L)
                .unitId(3L)
                .supplierId(5L)
                .purchasePrice(new BigDecimal("100000"))
                .universal(true)
                .specialPart(false)
                .build();

        Market market = Market.builder().id(2L).build();
        Unit unit = Unit.builder().id(3L).build();
        Supplier supplier = Supplier.builder().id(5L).build();

        when(marketRepo.findById(2L)).thenReturn(Optional.of(market));
        when(unitRepo.findById(3L)).thenReturn(Optional.of(unit));
        when(supplierRepo.findById(5L)).thenReturn(Optional.of(supplier));
        when(skuGenerator.generateSku(any(Part.class))).thenReturn("SKU123");

        when(partRepository.save(any(Part.class))).thenReturn(part);
        when(partMapper.toDto(part)).thenReturn(PartReqDto.builder().partId(1L).build());

        PartReqDto result = service.createPart(dto);

        assertNotNull(result);
        verify(categoryRepo, never()).findById(anyLong());
        verify(vehicleModelRepo, never()).findById(anyLong());
    }

    @Test
    void createPart_ShouldThrow_WhenMarketNotFound() {
        PartUpdateReqDto dto = PartUpdateReqDto.builder()
                .name("Oil 5W30")
                .marketId(2L)
                .unitId(3L)
                .supplierId(5L)
                .purchasePrice(new BigDecimal("100000"))
                .build();

        when(marketRepo.findById(2L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> service.createPart(dto));
    }

    @Test
    void updatePart_ShouldUpdateFieldsAndSave() {
        Part existing = Part.builder()
                .partId(1L)
                .name("Old")
                .purchasePrice(new BigDecimal("100000"))
                .build();

        PartUpdateReqDto dto = PartUpdateReqDto.builder()
                .name("New Name")
                .note("New note")
                .reorderLevel(10.0)
                .categoryId(1L)
                .marketId(2L)
                .unitId(3L)
                .vehicleModelId(4L)
                .purchasePrice(new BigDecimal("200000"))
                .sellingPrice(new BigDecimal("250000"))
                .supplierId(5L)
                .universal(true)
                .specialPart(true)
                .build();

        when(partRepository.findById(1L)).thenReturn(Optional.of(existing));

        PartCategory category = PartCategory.builder().id(1L).build();
        Market market = Market.builder().id(2L).build();
        Unit unit = Unit.builder().id(3L).build();
        VehicleModel model = VehicleModel.builder().vehicleModelId(4L).build();
        Supplier supplier = Supplier.builder().id(5L).build();

        when(categoryRepo.findById(1L)).thenReturn(Optional.of(category));
        when(marketRepo.findById(2L)).thenReturn(Optional.of(market));
        when(unitRepo.findById(3L)).thenReturn(Optional.of(unit));
        when(vehicleModelRepo.findById(4L)).thenReturn(Optional.of(model));
        when(supplierRepo.findById(5L)).thenReturn(Optional.of(supplier));

        when(skuGenerator.generateSku(existing)).thenReturn("SKU999");
        when(partRepository.save(existing)).thenReturn(existing);
        PartReqDto dtoResult = PartReqDto.builder().partId(1L).name("New Name").build();
        when(partMapper.toDto(existing)).thenReturn(dtoResult);

        PartReqDto result = service.updatePart(1L, dto);

        assertSame(dtoResult, result);
        assertEquals("New Name", existing.getName());
        assertEquals("New note", existing.getNote());
        assertEquals(10.0, existing.getReorderLevel());
        assertEquals(category, existing.getCategory());
        assertEquals(market, existing.getMarket());
        assertEquals(unit, existing.getUnit());
        assertEquals(model, existing.getVehicleModel());
        assertEquals(supplier, existing.getSupplier());
        assertEquals(new BigDecimal("200000"), existing.getPurchasePrice());
        assertEquals(new BigDecimal("250000"), existing.getSellingPrice());
        assertTrue(existing.isUniversal());
        assertTrue(existing.isSpecialPart());
        assertEquals("SKU999", existing.getSku());

        verify(partRepository).save(existing);
    }

    @Test
    void updatePart_ShouldThrow_WhenPartNotFound() {
        when(partRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class,
                () -> service.updatePart(1L, PartUpdateReqDto.builder().build()));
    }

    @Test
    void getPartByCategory_ShouldUseRepositoryAndMapper() {
        Long categoryId = 1L;
        Pageable pageable = PageRequest.of(0, 5);
        Page<Part> page = new PageImpl<>(List.of(part), pageable, 1);
        when(partRepository.findByCategory_Id(categoryId, pageable)).thenReturn(page);

        PartReqDto dto = PartReqDto.builder().partId(1L).name("Oil 5W30").build();
        when(partMapper.toDto(part)).thenReturn(dto);

        Page<PartReqDto> result = service.getPartByCategory(categoryId, 0, 5);

        assertEquals(1, result.getTotalElements());
        assertSame(dto, result.getContent().get(0));
        verify(partRepository).findByCategory_Id(categoryId, pageable);
        verify(partMapper).toDto(part);
    }

    // ========== Tests for getAllPart with filters ==========

    @Test
    void getAllPart_WithCategoryIdAndStatus_ShouldUseCorrectRepository() {
        Long categoryId = 1L;
        StockLevelStatus status = StockLevelStatus.IN_STOCK;
        Pageable pageable = PageRequest.of(0, 5);
        Page<Part> page = new PageImpl<>(List.of(part), pageable, 1);
        
        when(partRepository.findByCategory_IdAndStatus(categoryId, status, pageable)).thenReturn(page);
        when(partMapper.toDto(part)).thenReturn(PartReqDto.builder().partId(1L).build());

        Page<PartReqDto> result = service.getAllPart(0, 5, categoryId, status);

        assertEquals(1, result.getTotalElements());
        verify(partRepository).findByCategory_IdAndStatus(categoryId, status, pageable);
        verify(partRepository, never()).findByCategory_Id(anyLong(), any(Pageable.class));
        verify(partRepository, never()).findByStatus(any(StockLevelStatus.class), any(Pageable.class));
        verify(partRepository, never()).findAll(any(Pageable.class));
    }

    @Test
    void getAllPart_WithCategoryIdOnly_ShouldUseCorrectRepository() {
        Long categoryId = 1L;
        Pageable pageable = PageRequest.of(0, 5);
        Page<Part> page = new PageImpl<>(List.of(part), pageable, 1);
        
        when(partRepository.findByCategory_Id(categoryId, pageable)).thenReturn(page);
        when(partMapper.toDto(part)).thenReturn(PartReqDto.builder().partId(1L).build());

        Page<PartReqDto> result = service.getAllPart(0, 5, categoryId, null);

        assertEquals(1, result.getTotalElements());
        verify(partRepository).findByCategory_Id(categoryId, pageable);
        verify(partRepository, never()).findByCategory_IdAndStatus(anyLong(), any(), any(Pageable.class));
        verify(partRepository, never()).findByStatus(any(StockLevelStatus.class), any(Pageable.class));
        verify(partRepository, never()).findAll(any(Pageable.class));
    }

    @Test
    void getAllPart_WithStatusOnly_ShouldUseCorrectRepository() {
        StockLevelStatus status = StockLevelStatus.LOW_STOCK;
        Pageable pageable = PageRequest.of(0, 5);
        Page<Part> page = new PageImpl<>(List.of(part), pageable, 1);
        
        when(partRepository.findByStatus(status, pageable)).thenReturn(page);
        when(partMapper.toDto(part)).thenReturn(PartReqDto.builder().partId(1L).build());

        Page<PartReqDto> result = service.getAllPart(0, 5, null, status);

        assertEquals(1, result.getTotalElements());
        verify(partRepository).findByStatus(status, pageable);
        verify(partRepository, never()).findByCategory_IdAndStatus(anyLong(), any(), any(Pageable.class));
        verify(partRepository, never()).findByCategory_Id(anyLong(), any(Pageable.class));
        verify(partRepository, never()).findAll(any(Pageable.class));
    }

    @Test
    void getAllPart_WithNoFilters_ShouldUseFindAll() {
        Pageable pageable = PageRequest.of(0, 5);
        Page<Part> page = new PageImpl<>(List.of(part), pageable, 1);
        
        when(partRepository.findAll(pageable)).thenReturn(page);
        when(partMapper.toDto(part)).thenReturn(PartReqDto.builder().partId(1L).build());

        Page<PartReqDto> result = service.getAllPart(0, 5, null, null);

        assertEquals(1, result.getTotalElements());
        verify(partRepository).findAll(pageable);
        verify(partRepository, never()).findByCategory_IdAndStatus(anyLong(), any(), any(Pageable.class));
        verify(partRepository, never()).findByCategory_Id(anyLong(), any(Pageable.class));
        verify(partRepository, never()).findByStatus(any(StockLevelStatus.class), any(Pageable.class));
    }

    @Test
    void getAllPart_WithFilters_ShouldUpdateStockStatus() {
        Part partWithLowQty = Part.builder()
                .partId(1L)
                .quantityInStock(5.0)
                .reorderLevel(10.0)
                .status(StockLevelStatus.IN_STOCK)
                .build();

        Long categoryId = 1L;
        StockLevelStatus status = StockLevelStatus.IN_STOCK;
        Pageable pageable = PageRequest.of(0, 5);
        Page<Part> page = new PageImpl<>(List.of(partWithLowQty), pageable, 1);
        
        when(partRepository.findByCategory_IdAndStatus(categoryId, status, pageable)).thenReturn(page);
        when(partRepository.save(partWithLowQty)).thenReturn(partWithLowQty);
        when(partMapper.toDto(partWithLowQty)).thenReturn(PartReqDto.builder().partId(1L).build());

        service.getAllPart(0, 5, categoryId, status);

        assertEquals(StockLevelStatus.LOW_STOCK, partWithLowQty.getStatus());
        verify(partRepository).save(partWithLowQty);
    }

    // ========== Tests for createPart exception cases ==========

    @Test
    void createPart_ShouldThrow_WhenCategoryNotFound() {
        PartUpdateReqDto dto = PartUpdateReqDto.builder()
                .name("Oil 5W30")
                .categoryId(999L)
                .marketId(2L)
                .unitId(3L)
                .supplierId(5L)
                .purchasePrice(new BigDecimal("100000"))
                .build();

        when(categoryRepo.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.createPart(dto));
        verify(categoryRepo).findById(999L);
    }

    @Test
    void createPart_ShouldThrow_WhenUnitNotFound() {
        PartUpdateReqDto dto = PartUpdateReqDto.builder()
                .name("Oil 5W30")
                .marketId(2L)
                .unitId(999L)
                .supplierId(5L)
                .purchasePrice(new BigDecimal("100000"))
                .build();

        Market market = Market.builder().id(2L).build();
        when(marketRepo.findById(2L)).thenReturn(Optional.of(market));
        when(unitRepo.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.createPart(dto));
        verify(unitRepo).findById(999L);
    }

    @Test
    void createPart_ShouldThrow_WhenSupplierNotFound() {
        PartUpdateReqDto dto = PartUpdateReqDto.builder()
                .name("Oil 5W30")
                .marketId(2L)
                .unitId(3L)
                .supplierId(999L)
                .purchasePrice(new BigDecimal("100000"))
                .build();

        Market market = Market.builder().id(2L).build();
        Unit unit = Unit.builder().id(3L).build();
        when(marketRepo.findById(2L)).thenReturn(Optional.of(market));
        when(unitRepo.findById(3L)).thenReturn(Optional.of(unit));
        when(supplierRepo.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.createPart(dto));
        verify(supplierRepo).findById(999L);
    }

    @Test
    void createPart_ShouldThrow_WhenVehicleModelNotFound() {
        PartUpdateReqDto dto = PartUpdateReqDto.builder()
                .name("Oil 5W30")
                .marketId(2L)
                .unitId(3L)
                .vehicleModelId(999L)
                .supplierId(5L)
                .purchasePrice(new BigDecimal("100000"))
                .universal(false)
                .build();

        Market market = Market.builder().id(2L).build();
        Unit unit = Unit.builder().id(3L).build();
        Supplier supplier = Supplier.builder().id(5L).build();
        when(marketRepo.findById(2L)).thenReturn(Optional.of(market));
        when(unitRepo.findById(3L)).thenReturn(Optional.of(unit));
        when(supplierRepo.findById(5L)).thenReturn(Optional.of(supplier));
        when(vehicleModelRepo.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.createPart(dto));
        verify(vehicleModelRepo).findById(999L);
    }

    // ========== Tests for updatePart exception cases ==========

    @Test
    void updatePart_ShouldThrow_WhenCategoryNotFound() {
        Part existing = Part.builder().partId(1L).name("Old").build();
        PartUpdateReqDto dto = PartUpdateReqDto.builder()
                .categoryId(999L)
                .build();

        when(partRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(categoryRepo.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.updatePart(1L, dto));
        verify(categoryRepo).findById(999L);
    }

    @Test
    void updatePart_ShouldThrow_WhenMarketNotFound() {
        Part existing = Part.builder().partId(1L).name("Old").build();
        PartUpdateReqDto dto = PartUpdateReqDto.builder()
                .marketId(999L)
                .build();

        when(partRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(marketRepo.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.updatePart(1L, dto));
        verify(marketRepo).findById(999L);
    }

    @Test
    void updatePart_ShouldThrow_WhenUnitNotFound() {
        Part existing = Part.builder().partId(1L).name("Old").build();
        PartUpdateReqDto dto = PartUpdateReqDto.builder()
                .unitId(999L)
                .build();

        when(partRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(unitRepo.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.updatePart(1L, dto));
        verify(unitRepo).findById(999L);
    }

    @Test
    void updatePart_ShouldThrow_WhenVehicleModelNotFound() {
        Part existing = Part.builder().partId(1L).name("Old").build();
        PartUpdateReqDto dto = PartUpdateReqDto.builder()
                .vehicleModelId(999L)
                .build();

        when(partRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(vehicleModelRepo.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.updatePart(1L, dto));
        verify(vehicleModelRepo).findById(999L);
    }

    @Test
    void updatePart_ShouldThrow_WhenSupplierNotFound() {
        Part existing = Part.builder().partId(1L).name("Old").build();
        PartUpdateReqDto dto = PartUpdateReqDto.builder()
                .supplierId(999L)
                .build();

        when(partRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(supplierRepo.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.updatePart(1L, dto));
        verify(supplierRepo).findById(999L);
    }

    // ========== Tests for updatePart with null optional fields ==========

    @Test
    void updatePart_ShouldOnlyUpdateProvidedFields() {
        Part existing = Part.builder()
                .partId(1L)
                .name("Old Name")
                .note("Old note")
                .purchasePrice(new BigDecimal("100000"))
                .build();

        PartUpdateReqDto dto = PartUpdateReqDto.builder()
                .name("New Name")
                .build();

        when(partRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(skuGenerator.generateSku(existing)).thenReturn("SKU999");
        when(partRepository.save(existing)).thenReturn(existing);
        when(partMapper.toDto(existing)).thenReturn(PartReqDto.builder().partId(1L).build());

        service.updatePart(1L, dto);

        assertEquals("New Name", existing.getName());
        assertEquals("Old note", existing.getNote()); // Should remain unchanged
        assertEquals(new BigDecimal("100000"), existing.getPurchasePrice()); // Should remain unchanged
        verify(categoryRepo, never()).findById(anyLong());
        verify(marketRepo, never()).findById(anyLong());
        verify(unitRepo, never()).findById(anyLong());
        verify(vehicleModelRepo, never()).findById(anyLong());
        verify(supplierRepo, never()).findById(anyLong());
    }

    @Test
    void updatePart_ShouldUpdatePurchasePriceAndCalculateSellingPrice() {
        Part existing = Part.builder()
                .partId(1L)
                .purchasePrice(new BigDecimal("100000"))
                .sellingPrice(new BigDecimal("110000"))
                .build();

        PartUpdateReqDto dto = PartUpdateReqDto.builder()
                .purchasePrice(new BigDecimal("200000"))
                .build();

        when(partRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(skuGenerator.generateSku(existing)).thenReturn("SKU999");
        when(partRepository.save(existing)).thenReturn(existing);
        when(partMapper.toDto(existing)).thenReturn(PartReqDto.builder().partId(1L).build());

        service.updatePart(1L, dto);

        assertEquals(new BigDecimal("200000"), existing.getPurchasePrice());
        assertEquals(new BigDecimal("220000"), existing.getSellingPrice()); // 200000 * 1.10
    }

    @Test
    void updatePart_ShouldOverrideSellingPriceWhenProvided() {
        Part existing = Part.builder()
                .partId(1L)
                .purchasePrice(new BigDecimal("100000"))
                .sellingPrice(new BigDecimal("110000"))
                .build();

        PartUpdateReqDto dto = PartUpdateReqDto.builder()
                .purchasePrice(new BigDecimal("200000"))
                .sellingPrice(new BigDecimal("300000")) // Override
                .build();

        when(partRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(skuGenerator.generateSku(existing)).thenReturn("SKU999");
        when(partRepository.save(existing)).thenReturn(existing);
        when(partMapper.toDto(existing)).thenReturn(PartReqDto.builder().partId(1L).build());

        service.updatePart(1L, dto);

        assertEquals(new BigDecimal("200000"), existing.getPurchasePrice());
        assertEquals(new BigDecimal("300000"), existing.getSellingPrice()); // Overridden value
    }

    // ========== Tests for updateStockLevelStatusIfNeeded ==========

    @Test
    void getAllPart_ShouldUpdateStockStatus_WhenQuantityIsZero() {
        Part partWithZeroQty = Part.builder()
                .partId(1L)
                .quantityInStock(0.0)
                .reorderLevel(10.0)
                .status(StockLevelStatus.IN_STOCK)
                .build();

        Pageable pageable = PageRequest.of(0, 5);
        Page<Part> page = new PageImpl<>(List.of(partWithZeroQty), pageable, 1);
        when(partRepository.findAll(pageable)).thenReturn(page);
        when(partRepository.save(partWithZeroQty)).thenReturn(partWithZeroQty);
        when(partMapper.toDto(partWithZeroQty)).thenReturn(PartReqDto.builder().partId(1L).build());

        service.getAllPart(0, 5);

        assertEquals(StockLevelStatus.OUT_OF_STOCK, partWithZeroQty.getStatus());
        verify(partRepository).save(partWithZeroQty);
    }

    @Test
    void getAllPart_ShouldUpdateStockStatus_WhenQuantityIsNegative() {
        Part partWithNegativeQty = Part.builder()
                .partId(1L)
                .quantityInStock(-5.0)
                .reorderLevel(10.0)
                .status(StockLevelStatus.IN_STOCK)
                .build();

        Pageable pageable = PageRequest.of(0, 5);
        Page<Part> page = new PageImpl<>(List.of(partWithNegativeQty), pageable, 1);
        when(partRepository.findAll(pageable)).thenReturn(page);
        when(partRepository.save(partWithNegativeQty)).thenReturn(partWithNegativeQty);
        when(partMapper.toDto(partWithNegativeQty)).thenReturn(PartReqDto.builder().partId(1L).build());

        service.getAllPart(0, 5);

        assertEquals(StockLevelStatus.OUT_OF_STOCK, partWithNegativeQty.getStatus());
        verify(partRepository).save(partWithNegativeQty);
    }

    @Test
    void getAllPart_ShouldUpdateStockStatus_WhenQuantityIsLow() {
        Part partWithLowQty = Part.builder()
                .partId(1L)
                .quantityInStock(5.0)
                .reorderLevel(10.0)
                .status(StockLevelStatus.IN_STOCK)
                .build();

        Pageable pageable = PageRequest.of(0, 5);
        Page<Part> page = new PageImpl<>(List.of(partWithLowQty), pageable, 1);
        when(partRepository.findAll(pageable)).thenReturn(page);
        when(partRepository.save(partWithLowQty)).thenReturn(partWithLowQty);
        when(partMapper.toDto(partWithLowQty)).thenReturn(PartReqDto.builder().partId(1L).build());

        service.getAllPart(0, 5);

        assertEquals(StockLevelStatus.LOW_STOCK, partWithLowQty.getStatus());
        verify(partRepository).save(partWithLowQty);
    }

    @Test
    void getAllPart_ShouldUpdateStockStatus_WhenQuantityEqualsReorderLevel() {
        Part partAtReorderLevel = Part.builder()
                .partId(1L)
                .quantityInStock(10.0)
                .reorderLevel(10.0)
                .status(StockLevelStatus.IN_STOCK)
                .build();

        Pageable pageable = PageRequest.of(0, 5);
        Page<Part> page = new PageImpl<>(List.of(partAtReorderLevel), pageable, 1);
        when(partRepository.findAll(pageable)).thenReturn(page);
        when(partRepository.save(partAtReorderLevel)).thenReturn(partAtReorderLevel);
        when(partMapper.toDto(partAtReorderLevel)).thenReturn(PartReqDto.builder().partId(1L).build());

        service.getAllPart(0, 5);

        assertEquals(StockLevelStatus.LOW_STOCK, partAtReorderLevel.getStatus());
        verify(partRepository).save(partAtReorderLevel);
    }

    @Test
    void getAllPart_ShouldUpdateStockStatus_WhenQuantityIsAboveReorderLevel() {
        Part partWithGoodQty = Part.builder()
                .partId(1L)
                .quantityInStock(20.0)
                .reorderLevel(10.0)
                .status(StockLevelStatus.LOW_STOCK)
                .build();

        Pageable pageable = PageRequest.of(0, 5);
        Page<Part> page = new PageImpl<>(List.of(partWithGoodQty), pageable, 1);
        when(partRepository.findAll(pageable)).thenReturn(page);
        when(partRepository.save(partWithGoodQty)).thenReturn(partWithGoodQty);
        when(partMapper.toDto(partWithGoodQty)).thenReturn(PartReqDto.builder().partId(1L).build());

        service.getAllPart(0, 5);

        assertEquals(StockLevelStatus.IN_STOCK, partWithGoodQty.getStatus());
        verify(partRepository).save(partWithGoodQty);
    }

    @Test
    void getAllPart_ShouldNotSave_WhenStatusDoesNotChange() {
        Part partWithCorrectStatus = Part.builder()
                .partId(1L)
                .quantityInStock(20.0)
                .reorderLevel(10.0)
                .status(StockLevelStatus.IN_STOCK)
                .build();

        Pageable pageable = PageRequest.of(0, 5);
        Page<Part> page = new PageImpl<>(List.of(partWithCorrectStatus), pageable, 1);
        when(partRepository.findAll(pageable)).thenReturn(page);
        when(partMapper.toDto(partWithCorrectStatus)).thenReturn(PartReqDto.builder().partId(1L).build());

        service.getAllPart(0, 5);

        assertEquals(StockLevelStatus.IN_STOCK, partWithCorrectStatus.getStatus());
        verify(partRepository, never()).save(partWithCorrectStatus);
    }

    @Test
    void getAllPart_ShouldHandleNullQuantityInStock() {
        Part partWithNullQty = Part.builder()
                .partId(1L)
                .quantityInStock(null)
                .reorderLevel(10.0)
                .status(StockLevelStatus.IN_STOCK)
                .build();

        Pageable pageable = PageRequest.of(0, 5);
        Page<Part> page = new PageImpl<>(List.of(partWithNullQty), pageable, 1);
        when(partRepository.findAll(pageable)).thenReturn(page);
        when(partRepository.save(partWithNullQty)).thenReturn(partWithNullQty);
        when(partMapper.toDto(partWithNullQty)).thenReturn(PartReqDto.builder().partId(1L).build());

        service.getAllPart(0, 5);

        assertEquals(StockLevelStatus.OUT_OF_STOCK, partWithNullQty.getStatus());
        verify(partRepository).save(partWithNullQty);
    }

    @Test
    void getAllPart_ShouldHandleNullReorderLevel() {
        Part partWithNullReorderLevel = Part.builder()
                .partId(1L)
                .quantityInStock(5.0)
                .reorderLevel(null)
                .status(StockLevelStatus.IN_STOCK)
                .build();

        Pageable pageable = PageRequest.of(0, 5);
        Page<Part> page = new PageImpl<>(List.of(partWithNullReorderLevel), pageable, 1);
        when(partRepository.findAll(pageable)).thenReturn(page);
        when(partRepository.save(partWithNullReorderLevel)).thenReturn(partWithNullReorderLevel);
        when(partMapper.toDto(partWithNullReorderLevel)).thenReturn(PartReqDto.builder().partId(1L).build());

        service.getAllPart(0, 5);

        assertEquals(StockLevelStatus.IN_STOCK, partWithNullReorderLevel.getStatus()); // 5.0 > 0.0
        verify(partRepository).save(partWithNullReorderLevel);
    }

    @Test
    void getPartById_ShouldUpdateStockStatus_WhenQuantityIsLow() {
        Part partWithLowQty = Part.builder()
                .partId(1L)
                .quantityInStock(5.0)
                .reorderLevel(10.0)
                .status(StockLevelStatus.IN_STOCK)
                .build();

        when(partRepository.findById(1L)).thenReturn(Optional.of(partWithLowQty));
        when(partRepository.save(partWithLowQty)).thenReturn(partWithLowQty);
        when(partMapper.toDto(partWithLowQty)).thenReturn(PartReqDto.builder().partId(1L).build());

        service.getPartById(1L);

        assertEquals(StockLevelStatus.LOW_STOCK, partWithLowQty.getStatus());
        verify(partRepository).save(partWithLowQty);
    }

    @Test
    void getPartById_ShouldNotUpdateStockStatus_WhenPartIsNull() {
        when(partRepository.findById(1L)).thenReturn(Optional.empty());
        when(partMapper.toDto(null)).thenReturn(null);

        PartReqDto result = service.getPartById(1L);

        assertNull(result);
        verify(partRepository, never()).save(any(Part.class));
    }
}


