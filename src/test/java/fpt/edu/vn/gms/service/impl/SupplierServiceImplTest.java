package fpt.edu.vn.gms.service.impl;

import fpt.edu.vn.gms.base.BaseServiceTest;
import fpt.edu.vn.gms.dto.request.SupplierRequestDto;
import fpt.edu.vn.gms.dto.response.SupplierResponseDto;
import fpt.edu.vn.gms.entity.Supplier;
import fpt.edu.vn.gms.exception.ResourceNotFoundException;
import fpt.edu.vn.gms.mapper.SupplierMapper;
import fpt.edu.vn.gms.repository.SupplierRepository;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;

import java.util.Optional;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class SupplierServiceImplTest extends BaseServiceTest {

  @Mock
  private SupplierRepository supplierRepository;

  @Mock
  private SupplierMapper supplierMapper;

  @InjectMocks
  private SupplierServiceImpl supplierServiceImpl;

  @Test
  void getAllSuppliers_WhenSuppliersExist_ShouldReturnPagedDtos() {
    Supplier supplier = Supplier.builder().id(1L).name("ABC").isActive(true).build();
    SupplierResponseDto dto = SupplierResponseDto.builder().id(1L).name("ABC").build();
    Page<Supplier> page = new PageImpl<>(List.of(supplier));
    when(supplierRepository.findAll(PageRequest.of(0, 10))).thenReturn(page);
    when(supplierMapper.toResponseDto(supplier)).thenReturn(dto);

    Page<SupplierResponseDto> result = supplierServiceImpl.getAllSuppliers(0, 10);

    assertEquals(1, result.getTotalElements());
    assertEquals("ABC", result.getContent().get(0).getName());
    verify(supplierRepository).findAll(PageRequest.of(0, 10));
  }

  @Test
  void getAllSuppliers_WhenNoSuppliersExist_ShouldReturnEmptyPage() {
    Page<Supplier> page = new PageImpl<>(List.of());
    when(supplierRepository.findAll(PageRequest.of(0, 10))).thenReturn(page);

    Page<SupplierResponseDto> result = supplierServiceImpl.getAllSuppliers(0, 10);

    assertTrue(result.isEmpty());
    verify(supplierRepository).findAll(PageRequest.of(0, 10));
  }

  @Test
  void getSupplierById_WhenSupplierExists_ShouldReturnDto() {
    Supplier supplier = Supplier.builder().id(1L).name("ABC").isActive(true).build();
    SupplierResponseDto dto = SupplierResponseDto.builder().id(1L).name("ABC").build();
    when(supplierRepository.findById(1L)).thenReturn(Optional.of(supplier));
    when(supplierMapper.toResponseDto(supplier)).thenReturn(dto);

    SupplierResponseDto result = supplierServiceImpl.getSupplierById(1L);

    assertNotNull(result);
    assertEquals("ABC", result.getName());
    verify(supplierRepository).findById(1L);
  }

  @Test
  void getSupplierById_WhenSupplierNotFound_ShouldThrowResourceNotFoundException() {
    when(supplierRepository.findById(99L)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> supplierServiceImpl.getSupplierById(99L));
    verify(supplierRepository).findById(99L);
  }

  @Test
  void createSupplier_WhenValidDto_ShouldSaveAndReturnDto() {
    SupplierRequestDto req = SupplierRequestDto.builder().name("DEF").build();
    Supplier supplier = Supplier.builder().id(2L).name("DEF").isActive(true).build();
    SupplierResponseDto dto = SupplierResponseDto.builder().id(2L).name("DEF").build();

    when(supplierMapper.toEntity(req)).thenReturn(supplier);
    when(supplierRepository.save(supplier)).thenReturn(supplier);
    when(supplierMapper.toResponseDto(supplier)).thenReturn(dto);

    SupplierResponseDto result = supplierServiceImpl.createSupplier(req);

    assertNotNull(result);
    assertEquals("DEF", result.getName());
    verify(supplierRepository).save(supplier);
  }

  @Test
  void updateSupplier_WhenSupplierExists_ShouldUpdateAndReturnDto() {
    SupplierRequestDto req = SupplierRequestDto.builder().name("XYZ").build();
    Supplier existing = Supplier.builder().id(3L).name("OLD").isActive(true).build();
    Supplier updated = Supplier.builder().id(3L).name("XYZ").isActive(true).build();
    SupplierResponseDto dto = SupplierResponseDto.builder().id(3L).name("XYZ").build();

    when(supplierRepository.findById(3L)).thenReturn(Optional.of(existing));
    doAnswer(invocation -> {
      SupplierRequestDto r = invocation.getArgument(0);
      Supplier s = invocation.getArgument(1);
      s.setName(r.getName());
      return null;
    }).when(supplierMapper).updateSupplierFromDto(req, existing);
    when(supplierRepository.save(existing)).thenReturn(updated);
    when(supplierMapper.toResponseDto(updated)).thenReturn(dto);

    SupplierResponseDto result = supplierServiceImpl.updateSupplier(3L, req);

    assertNotNull(result);
    assertEquals("XYZ", result.getName());
    verify(supplierRepository).save(existing);
  }

  @Test
  void updateSupplier_WhenSupplierNotFound_ShouldThrowResourceNotFoundException() {
    SupplierRequestDto req = SupplierRequestDto.builder().name("XYZ").build();
    when(supplierRepository.findById(99L)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> supplierServiceImpl.updateSupplier(99L, req));
    verify(supplierRepository).findById(99L);
  }

  @Test
  void softDeleteSupplier_WhenSupplierExists_ShouldSetInactive() {
    Supplier supplier = Supplier.builder().id(4L).name("DEL").isActive(true).build();
    when(supplierRepository.findById(4L)).thenReturn(Optional.of(supplier));
    when(supplierRepository.save(supplier)).thenReturn(supplier);

    supplierServiceImpl.softDeleteSupplier(4L);

    assertFalse(supplier.getIsActive());
    verify(supplierRepository).save(supplier);
  }

  @Test
  void softDeleteSupplier_WhenSupplierNotFound_ShouldThrowResourceNotFoundException() {
    when(supplierRepository.findById(99L)).thenReturn(Optional.empty());

    assertThrows(ResourceNotFoundException.class, () -> supplierServiceImpl.softDeleteSupplier(99L));
    verify(supplierRepository).findById(99L);
  }
}
