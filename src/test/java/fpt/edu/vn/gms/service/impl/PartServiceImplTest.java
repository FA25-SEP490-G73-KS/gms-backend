package fpt.edu.vn.gms.service.impl;

import fpt.edu.vn.gms.base.BaseServiceTest;
import fpt.edu.vn.gms.dto.request.PartResDto;
import fpt.edu.vn.gms.dto.request.PartUpdateReqDto;
import fpt.edu.vn.gms.dto.response.PartReqDto;
import fpt.edu.vn.gms.entity.*;
import fpt.edu.vn.gms.exception.ResourceNotFoundException;
import fpt.edu.vn.gms.mapper.PartMapper;
import fpt.edu.vn.gms.repository.*;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PartServiceImplTest extends BaseServiceTest {

  @Mock
  private PartRepository partRepository;
  @Mock
  private CategoryRepository categoryRepo;
  @Mock
  private MarketRepository marketRepo;
  @Mock
  private UnitRepository unitRepo;
  @Mock
  private VehicleModelRepository vehicleModelRepo;
  @Mock
  private PartMapper partMapper;

  @InjectMocks
  private PartServiceImpl partServiceImpl;

  @Test
  void getAllPart_WhenPartsExist_ShouldReturnPagedPartReqDto() {
    Part part = Part.builder().partId(1L).name("Bugi").build();
    PartReqDto dto = PartReqDto.builder().partId(1L).name("Bugi").build();
    Page<Part> page = new PageImpl<>(List.of(part));
    when(partRepository.findAll(any(Pageable.class))).thenReturn(page);
    when(partMapper.toDto(part)).thenReturn(dto);

    Page<PartReqDto> result = partServiceImpl.getAllPart(0, 10);

    assertEquals(1, result.getTotalElements());
    assertEquals("Bugi", result.getContent().get(0).getName());
  }

  @Test
  void getAllPart_WhenNoPartsExist_ShouldReturnEmptyPage() {
    Page<Part> page = new PageImpl<>(List.of());
    when(partRepository.findAll(any(Pageable.class))).thenReturn(page);

    Page<PartReqDto> result = partServiceImpl.getAllPart(0, 10);

    assertTrue(result.isEmpty());
  }

  @Test
  void createPart_WhenAllValid_ShouldSaveAndReturnDto() {
    PartResDto req = PartResDto.builder()
        .name("Bugi")
        .categoryId(1L)
        .vehicleModel(2L)
        .marketId(3L)
        .purchasePrice(BigDecimal.valueOf(100000))
        .quantity(10D)
        .unitId(4L)
        .universal(true)
        .specialPart(false)
        .note("Ghi chú")
        .build();

    PartCategory category = PartCategory.builder().id(1L).name("Động cơ").build();
    Market market = Market.builder().id(3L).name("VN").build();
    Unit unit = Unit.builder().id(4L).name("Cái").build();
    VehicleModel model = VehicleModel.builder().vehicleModelId(2L).name("Honda").build();

    Part part = Part.builder().partId(5L).name("Bugi").build();
    PartReqDto dto = PartReqDto.builder().partId(5L).name("Bugi").build();

    when(categoryRepo.findById(1L)).thenReturn(Optional.of(category));
    when(marketRepo.findById(3L)).thenReturn(Optional.of(market));
    when(unitRepo.findById(4L)).thenReturn(Optional.of(unit));
    when(vehicleModelRepo.findById(2L)).thenReturn(Optional.of(model));
    when(partRepository.save(any(Part.class))).thenReturn(part);
    when(partMapper.toDto(part)).thenReturn(dto);

    PartReqDto result = partServiceImpl.createPart(req);

    assertNotNull(result);
    assertEquals("Bugi", result.getName());
    verify(partRepository).save(any(Part.class));
  }

  @Test
  void createPart_WhenCategoryIdNull_ShouldAllowAndReturnDto() {
    PartResDto req = PartResDto.builder()
        .name("Bugi")
        .categoryId(null)
        .vehicleModel(2L)
        .marketId(3L)
        .purchasePrice(BigDecimal.valueOf(100000))
        .quantity(10D)
        .unitId(4L)
        .universal(true)
        .specialPart(false)
        .note("Ghi chú")
        .build();

    Market market = Market.builder().id(3L).name("VN").build();
    Unit unit = Unit.builder().id(4L).name("Cái").build();
    VehicleModel model = VehicleModel.builder().vehicleModelId(2L).name("Honda").build();

    Part part = Part.builder().partId(5L).name("Bugi").build();
    PartReqDto dto = PartReqDto.builder().partId(5L).name("Bugi").build();

    when(marketRepo.findById(3L)).thenReturn(Optional.of(market));
    when(unitRepo.findById(4L)).thenReturn(Optional.of(unit));
    when(vehicleModelRepo.findById(2L)).thenReturn(Optional.of(model));
    when(partRepository.save(any(Part.class))).thenReturn(part);
    when(partMapper.toDto(part)).thenReturn(dto);

    PartReqDto result = partServiceImpl.createPart(req);

    assertNotNull(result);
    assertEquals("Bugi", result.getName());
    verify(partRepository).save(any(Part.class));
  }

  @Test
  void createPart_WhenCategoryNotFound_ShouldThrowResourceNotFoundException() {
    PartResDto req = PartResDto.builder()
        .name("Bugi")
        .categoryId(1L)
        .vehicleModel(2L)
        .marketId(3L)
        .purchasePrice(BigDecimal.valueOf(100000))
        .quantity(10D)
        .unitId(4L)
        .universal(true)
        .specialPart(false)
        .note("Ghi chú")
        .build();

    when(categoryRepo.findById(1L)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> partServiceImpl.createPart(req));
  }

  @Test
  void createPart_WhenMarketNotFound_ShouldThrowResourceNotFoundException() {
    PartResDto req = PartResDto.builder()
        .name("Bugi")
        .categoryId(null)
        .vehicleModel(2L)
        .marketId(3L)
        .purchasePrice(BigDecimal.valueOf(100000))
        .quantity(10D)
        .unitId(4L)
        .universal(true)
        .specialPart(false)
        .note("Ghi chú")
        .build();

    when(marketRepo.findById(3L)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> partServiceImpl.createPart(req));
  }

  @Test
  void createPart_WhenUnitNotFound_ShouldThrowResourceNotFoundException() {
    PartResDto req = PartResDto.builder()
        .name("Bugi")
        .categoryId(null)
        .vehicleModel(2L)
        .marketId(3L)
        .purchasePrice(BigDecimal.valueOf(100000))
        .quantity(10D)
        .unitId(4L)
        .universal(true)
        .specialPart(false)
        .note("Ghi chú")
        .build();

    Market market = Market.builder().id(3L).name("VN").build();
    when(marketRepo.findById(3L)).thenReturn(Optional.of(market));
    when(unitRepo.findById(4L)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> partServiceImpl.createPart(req));
  }

  @Test
  void createPart_WhenVehicleModelNotFound_ShouldThrowResourceNotFoundException() {
    PartResDto req = PartResDto.builder()
        .name("Bugi")
        .categoryId(null)
        .vehicleModel(2L)
        .marketId(3L)
        .purchasePrice(BigDecimal.valueOf(100000))
        .quantity(10D)
        .unitId(4L)
        .universal(true)
        .specialPart(false)
        .note("Ghi chú")
        .build();

    Market market = Market.builder().id(3L).name("VN").build();
    Unit unit = Unit.builder().id(4L).name("Cái").build();
    when(marketRepo.findById(3L)).thenReturn(Optional.of(market));
    when(unitRepo.findById(4L)).thenReturn(Optional.of(unit));
    when(vehicleModelRepo.findById(2L)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> partServiceImpl.createPart(req));
  }

  @Test
  void updatePart_WhenAllValid_ShouldUpdateAndReturnDto() {
    PartUpdateReqDto req = PartUpdateReqDto.builder()
        .name("Bugi mới")
        .categoryId(1L)
        .vehicleModelId(2L)
        .marketId(3L)
        .purchasePrice(BigDecimal.valueOf(200000))
        .unitId(4L)
        .universal(false)
        .specialPart(true)
        .note("Đã cập nhật")
        .build();

    Part part = Part.builder().partId(5L).name("Bugi").build();
    PartCategory category = PartCategory.builder().id(1L).name("Động cơ").build();
    Market market = Market.builder().id(3L).name("VN").build();
    Unit unit = Unit.builder().id(4L).name("Cái").build();
    VehicleModel model = VehicleModel.builder().vehicleModelId(2L).name("Honda").build();

    Part updated = Part.builder().partId(5L).name("Bugi mới").build();
    PartReqDto dto = PartReqDto.builder().partId(5L).name("Bugi mới").build();

    when(partRepository.findById(5L)).thenReturn(Optional.of(part));
    when(categoryRepo.findById(1L)).thenReturn(Optional.of(category));
    when(marketRepo.findById(3L)).thenReturn(Optional.of(market));
    when(unitRepo.findById(4L)).thenReturn(Optional.of(unit));
    when(vehicleModelRepo.findById(2L)).thenReturn(Optional.of(model));
    when(partRepository.save(any(Part.class))).thenReturn(updated);
    when(partMapper.toDto(updated)).thenReturn(dto);

    PartReqDto result = partServiceImpl.updatePart(5L, req);

    assertNotNull(result);
    assertEquals("Bugi mới", result.getName());
    verify(partRepository).save(any(Part.class));
  }

  @Test
  void updatePart_WhenPartNotFound_ShouldThrowResourceNotFoundException() {
    PartUpdateReqDto req = PartUpdateReqDto.builder()
        .name("Bugi mới")
        .categoryId(1L)
        .vehicleModelId(2L)
        .marketId(3L)
        .purchasePrice(BigDecimal.valueOf(200000))
        .unitId(4L)
        .universal(false)
        .specialPart(true)
        .note("Đã cập nhật")
        .build();

    when(partRepository.findById(5L)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> partServiceImpl.updatePart(5L, req));
  }

  @Test
  void updatePart_WhenCategoryNotFound_ShouldThrowResourceNotFoundException() {
    PartUpdateReqDto req = PartUpdateReqDto.builder()
        .name("Bugi mới")
        .categoryId(1L)
        .vehicleModelId(2L)
        .marketId(3L)
        .purchasePrice(BigDecimal.valueOf(200000))
        .unitId(4L)
        .universal(false)
        .specialPart(true)
        .note("Đã cập nhật")
        .build();

    Part part = Part.builder().partId(5L).name("Bugi").build();
    when(partRepository.findById(5L)).thenReturn(Optional.of(part));
    when(categoryRepo.findById(1L)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> partServiceImpl.updatePart(5L, req));
  }

  @Test
  void updatePart_WhenMarketNotFound_ShouldThrowResourceNotFoundException() {
    PartUpdateReqDto req = PartUpdateReqDto.builder()
        .name("Bugi mới")
        .categoryId(1L)
        .vehicleModelId(2L)
        .marketId(3L)
        .purchasePrice(BigDecimal.valueOf(200000))
        .unitId(4L)
        .universal(false)
        .specialPart(true)
        .note("Đã cập nhật")
        .build();

    Part part = Part.builder().partId(5L).name("Bugi").build();
    PartCategory category = PartCategory.builder().id(1L).name("Động cơ").build();
    when(partRepository.findById(5L)).thenReturn(Optional.of(part));
    when(categoryRepo.findById(1L)).thenReturn(Optional.of(category));
    when(marketRepo.findById(3L)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> partServiceImpl.updatePart(5L, req));
  }

  @Test
  void updatePart_WhenUnitNotFound_ShouldThrowResourceNotFoundException() {
    PartUpdateReqDto req = PartUpdateReqDto.builder()
        .name("Bugi mới")
        .categoryId(1L)
        .vehicleModelId(2L)
        .marketId(3L)
        .purchasePrice(BigDecimal.valueOf(200000))
        .unitId(4L)
        .universal(false)
        .specialPart(true)
        .note("Đã cập nhật")
        .build();

    Part part = Part.builder().partId(5L).name("Bugi").build();
    PartCategory category = PartCategory.builder().id(1L).name("Động cơ").build();
    Market market = Market.builder().id(3L).name("VN").build();
    when(partRepository.findById(5L)).thenReturn(Optional.of(part));
    when(categoryRepo.findById(1L)).thenReturn(Optional.of(category));
    when(marketRepo.findById(3L)).thenReturn(Optional.of(market));
    when(unitRepo.findById(4L)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> partServiceImpl.updatePart(5L, req));
  }

  @Test
  void updatePart_WhenVehicleModelNotFound_ShouldThrowResourceNotFoundException() {
    PartUpdateReqDto req = PartUpdateReqDto.builder()
        .name("Bugi mới")
        .categoryId(1L)
        .vehicleModelId(2L)
        .marketId(3L)
        .purchasePrice(BigDecimal.valueOf(200000))
        .unitId(4L)
        .universal(false)
        .specialPart(true)
        .note("Đã cập nhật")
        .build();

    Part part = Part.builder().partId(5L).name("Bugi").build();
    PartCategory category = PartCategory.builder().id(1L).name("Động cơ").build();
    Market market = Market.builder().id(3L).name("VN").build();
    Unit unit = Unit.builder().id(4L).name("Cái").build();
    when(partRepository.findById(5L)).thenReturn(Optional.of(part));
    when(categoryRepo.findById(1L)).thenReturn(Optional.of(category));
    when(marketRepo.findById(3L)).thenReturn(Optional.of(market));
    when(unitRepo.findById(4L)).thenReturn(Optional.of(unit));
    when(vehicleModelRepo.findById(2L)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> partServiceImpl.updatePart(5L, req));
  }

  @Test
  void getPartByCategory_WhenPartsExist_ShouldReturnPagedPartReqDto() {
    Part part = Part.builder().partId(1L).name("Bugi").build();
    PartReqDto dto = PartReqDto.builder().partId(1L).name("Bugi").build();
    Page<Part> page = new PageImpl<>(List.of(part));
    when(partRepository.findByCategory(eq("Động cơ"), any(Pageable.class))).thenReturn(page);
    when(partMapper.toDto(part)).thenReturn(dto);

    Page<PartReqDto> result = partServiceImpl.getPartByCategory("Động cơ", 0, 10);

    assertEquals(1, result.getTotalElements());
    assertEquals("Bugi", result.getContent().get(0).getName());
  }

  @Test
  void getPartByCategory_WhenNoPartsExist_ShouldReturnEmptyPage() {
    Page<Part> page = new PageImpl<>(List.of());
    when(partRepository.findByCategory(eq("Động cơ"), any(Pageable.class))).thenReturn(page);

    Page<PartReqDto> result = partServiceImpl.getPartByCategory("Động cơ", 0, 10);

    assertTrue(result.isEmpty());
  }
}
