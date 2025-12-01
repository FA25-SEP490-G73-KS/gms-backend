package fpt.edu.vn.gms.service.impl;

import fpt.edu.vn.gms.dto.request.UnitRequestDto;
import fpt.edu.vn.gms.dto.response.UnitResponseDto;
import fpt.edu.vn.gms.entity.Unit;
import fpt.edu.vn.gms.exception.ResourceNotFoundException;
import fpt.edu.vn.gms.mapper.UnitMapper;
import fpt.edu.vn.gms.repository.UnitRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UnitServiceImplTest {

    @Mock
    UnitRepository unitRepository;
    @Mock
    UnitMapper unitMapper;

    @InjectMocks
    UnitServiceImpl service;

    @Test
    void getAll_ShouldReturnPagedResponseDtos() {
        Pageable pageable = PageRequest.of(0, 5);
        Unit unit = Unit.builder().id(1L).name("Cái").build();
        Page<Unit> page = new PageImpl<>(List.of(unit), pageable, 1);
        when(unitRepository.findAll(pageable)).thenReturn(page);

        UnitResponseDto dto = new UnitResponseDto(1L, "Cái");
        when(unitMapper.toResponseDto(unit)).thenReturn(dto);

        Page<UnitResponseDto> result = service.getAll(pageable);

        assertEquals(1, result.getTotalElements());
        assertSame(dto, result.getContent().get(0));
        verify(unitRepository).findAll(pageable);
        verify(unitMapper).toResponseDto(unit);
    }

    @Test
    void getById_ShouldReturnDto_WhenFound() {
        Unit unit = Unit.builder().id(1L).name("Cái").build();
        when(unitRepository.findById(1L)).thenReturn(Optional.of(unit));

        UnitResponseDto dto = new UnitResponseDto(1L, "Cái");
        when(unitMapper.toResponseDto(unit)).thenReturn(dto);

        UnitResponseDto result = service.getById(1L);

        assertSame(dto, result);
        verify(unitRepository).findById(1L);
        verify(unitMapper).toResponseDto(unit);
    }

    @Test
    void getById_ShouldThrow_WhenNotFound() {
        when(unitRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.getById(1L));
    }

    @Test
    void create_ShouldMapAndSave() {
        UnitRequestDto req = new UnitRequestDto("Cái");

        Unit toSave = Unit.builder().name("Cái").build();
        Unit saved = Unit.builder().id(1L).name("Cái").build();

        when(unitRepository.save(any(Unit.class))).thenReturn(saved);

        UnitResponseDto dto = new UnitResponseDto(1L, "Cái");
        when(unitMapper.toResponseDto(saved)).thenReturn(dto);

        UnitResponseDto result = service.create(req);

        assertSame(dto, result);
        verify(unitRepository).save(any(Unit.class));
        verify(unitMapper).toResponseDto(saved);
    }

    @Test
    void update_ShouldUpdateAndSave_WhenFound() {
        Unit existing = Unit.builder().id(1L).name("Old").build();
        when(unitRepository.findById(1L)).thenReturn(Optional.of(existing));

        UnitRequestDto req = new UnitRequestDto("New");

        Unit updated = Unit.builder().id(1L).name("New").build();
        when(unitRepository.save(existing)).thenReturn(updated);

        UnitResponseDto dto = new UnitResponseDto(1L, "New");
        when(unitMapper.toResponseDto(updated)).thenReturn(dto);

        UnitResponseDto result = service.update(1L, req);

        assertSame(dto, result);
        assertEquals("New", existing.getName());
        verify(unitRepository).findById(1L);
        verify(unitRepository).save(existing);
        verify(unitMapper).toResponseDto(updated);
    }

    @Test
    void update_ShouldThrow_WhenNotFound() {
        when(unitRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> service.update(1L, new UnitRequestDto("New")));
    }

    @Test
    void delete_ShouldDelete_WhenExists() {
        when(unitRepository.existsById(1L)).thenReturn(true);

        service.delete(1L);

        verify(unitRepository).existsById(1L);
        verify(unitRepository).deleteById(1L);
    }

    @Test
    void delete_ShouldThrow_WhenNotExists() {
        when(unitRepository.existsById(1L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> service.delete(1L));
        verify(unitRepository).existsById(1L);
        verify(unitRepository, never()).deleteById(anyLong());
    }
}


