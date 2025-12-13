package fpt.edu.vn.gms.service.impl;

import fpt.edu.vn.gms.dto.request.SupplierRequestDto;
import fpt.edu.vn.gms.dto.response.SupplierResponseDto;
import fpt.edu.vn.gms.entity.Supplier;
import fpt.edu.vn.gms.exception.ResourceNotFoundException;
import fpt.edu.vn.gms.mapper.SupplierMapper;
import fpt.edu.vn.gms.repository.SupplierRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SupplierServiceImplTest {

    @Mock
    SupplierRepository supplierRepository;
    @Mock
    SupplierMapper supplierMapper;

    @InjectMocks
    SupplierServiceImpl service;

    @Test
    void getAllSuppliers_ShouldReturnPagedResponse() {
        Supplier supplier = Supplier.builder()
                .id(1L)
                .name("ABC")
                .phone("0901")
                .email("a@b.com")
                .address("HN")
                .isActive(true)
                .build();
        PageRequest pageRequest = PageRequest.of(0, 5);
        Page<Supplier> page = new PageImpl<>(List.of(supplier), pageRequest, 1);
        when(supplierRepository.findAll(pageRequest)).thenReturn(page);

        SupplierResponseDto dto = SupplierResponseDto.builder()
                .id(1L)
                .name("ABC")
                .phone("0901")
                .email("a@b.com")
                .address("HN")
                .isActive(true)
                .build();
        when(supplierMapper.toResponseDto(supplier)).thenReturn(dto);

        Page<SupplierResponseDto> result = service.getAllSuppliers(0, 5);

        assertEquals(1, result.getTotalElements());
        assertSame(dto, result.getContent().get(0));
        verify(supplierRepository).findAll(pageRequest);
        verify(supplierMapper).toResponseDto(supplier);
    }

    @Test
    void getSupplierById_ShouldReturnDto_WhenFound() {
        Supplier supplier = Supplier.builder()
                .id(1L)
                .name("ABC")
                .build();
        when(supplierRepository.findById(1L)).thenReturn(Optional.of(supplier));

        SupplierResponseDto dto = SupplierResponseDto.builder()
                .id(1L)
                .name("ABC")
                .build();
        when(supplierMapper.toResponseDto(supplier)).thenReturn(dto);

        SupplierResponseDto result = service.getSupplierById(1L);

        assertSame(dto, result);
        verify(supplierRepository).findById(1L);
        verify(supplierMapper).toResponseDto(supplier);
    }

    @Test
    void getSupplierById_ShouldThrow_WhenNotFound() {
        when(supplierRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.getSupplierById(1L));
    }

    @Test
    void createSupplier_ShouldMapAndSave() {
        SupplierRequestDto req = SupplierRequestDto.builder()
                .name("ABC")
                .phone("0901")
                .email("a@b.com")
                .address("HN")
                .build();

        Supplier entity = Supplier.builder()
                .id(1L)
                .name("ABC")
                .phone("0901")
                .email("a@b.com")
                .address("HN")
                .isActive(true)
                .build();

        when(supplierMapper.toEntity(req)).thenReturn(entity);
        when(supplierRepository.save(entity)).thenReturn(entity);

        SupplierResponseDto dto = SupplierResponseDto.builder()
                .id(1L)
                .name("ABC")
                .build();
        when(supplierMapper.toResponseDto(entity)).thenReturn(dto);

        SupplierResponseDto result = service.createSupplier(req);

        assertSame(dto, result);
        assertTrue(entity.getIsActive());
        verify(supplierMapper).toEntity(req);
        verify(supplierRepository).save(entity);
        verify(supplierMapper).toResponseDto(entity);
    }

    @Test
    void updateSupplier_ShouldUpdate_WhenFound() {
        Supplier existing = Supplier.builder()
                .id(1L)
                .name("Old")
                .build();
        when(supplierRepository.findById(1L)).thenReturn(Optional.of(existing));

        SupplierRequestDto req = SupplierRequestDto.builder()
                .name("New")
                .phone("0901")
                .build();

        Supplier updated = Supplier.builder()
                .id(1L)
                .name("New")
                .build();
        when(supplierRepository.save(existing)).thenReturn(updated);

        SupplierResponseDto dto = SupplierResponseDto.builder()
                .id(1L)
                .name("New")
                .build();
        when(supplierMapper.toResponseDto(updated)).thenReturn(dto);

        SupplierResponseDto result = service.updateSupplier(1L, req);

        assertSame(dto, result);
        verify(supplierRepository).findById(1L);
        verify(supplierMapper).updateSupplierFromDto(req, existing);
        verify(supplierRepository).save(existing);
        verify(supplierMapper).toResponseDto(updated);
    }

    @Test
    void updateSupplier_ShouldThrow_WhenNotFound() {
        when(supplierRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> service.updateSupplier(1L, SupplierRequestDto.builder().name("New").build()));
    }

    @Test
    void toggleSupplierActiveStatus_ShouldToggleStatus_WhenFound() {
        Supplier supplier = Supplier.builder()
                .id(1L)
                .name("ABC")
                .isActive(true)
                .build();
        when(supplierRepository.findById(1L)).thenReturn(Optional.of(supplier));

        SupplierResponseDto dto = SupplierResponseDto.builder()
                .id(1L)
                .name("ABC")
                .isActive(false)
                .build();
        when(supplierMapper.toResponseDto(any(Supplier.class))).thenReturn(dto);

        SupplierResponseDto result = service.toggleSupplierActiveStatus(1L);

        assertFalse(supplier.getIsActive());
        assertSame(dto, result);
        verify(supplierRepository).findById(1L);
        verify(supplierRepository).save(supplier);
        verify(supplierMapper).toResponseDto(any(Supplier.class));
    }

    @Test
    void toggleSupplierActiveStatus_ShouldThrow_WhenNotFound() {
        when(supplierRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.toggleSupplierActiveStatus(1L));
    }
}


