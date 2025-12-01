package fpt.edu.vn.gms.service.impl;

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
                .isUniversal(false)
                .isSpecialPart(true)
                .build();

        PartCategory category = PartCategory.builder().categoryId(1L).build();
        Market market = Market.builder().marketId(2L).build();
        Unit unit = Unit.builder().unitId(3L).build();
        VehicleModel model = VehicleModel.builder().vehicleModelId(4L).build();
        Supplier supplier = Supplier.builder().supplierId(5L).build();

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
                .isUniversal(true)
                .isSpecialPart(false)
                .build();

        Market market = Market.builder().marketId(2L).build();
        Unit unit = Unit.builder().unitId(3L).build();
        Supplier supplier = Supplier.builder().supplierId(5L).build();

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
                .isUniversal(true)
                .isSpecialPart(true)
                .build();

        when(partRepository.findById(1L)).thenReturn(Optional.of(existing));

        PartCategory category = PartCategory.builder().categoryId(1L).build();
        Market market = Market.builder().marketId(2L).build();
        Unit unit = Unit.builder().unitId(3L).build();
        VehicleModel model = VehicleModel.builder().vehicleModelId(4L).build();
        Supplier supplier = Supplier.builder().supplierId(5L).build();

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
        Pageable pageable = PageRequest.of(0, 5);
        Page<Part> page = new PageImpl<>(List.of(part), pageable, 1);
        when(partRepository.findByCategory("ENGINE", pageable)).thenReturn(page);

        PartReqDto dto = PartReqDto.builder().partId(1L).name("Oil 5W30").build();
        when(partMapper.toDto(part)).thenReturn(dto);

        Page<PartReqDto> result = service.getPartByCategory("ENGINE", 0, 5);

        assertEquals(1, result.getTotalElements());
        assertSame(dto, result.getContent().get(0));
        verify(partRepository).findByCategory("ENGINE", pageable);
        verify(partMapper).toDto(part);
    }
}


