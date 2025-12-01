package fpt.edu.vn.gms.service.impl;

import fpt.edu.vn.gms.entity.Brand;
import fpt.edu.vn.gms.repository.BrandRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BrandServiceImplTest {

    @Mock
    BrandRepository brandRepository;

    @InjectMocks
    BrandServiceImpl service;

    @Test
    void getAll_ShouldReturnAllBrands() {
        Brand b1 = Brand.builder().brandId(1L).name("Toyota").build();
        Brand b2 = Brand.builder().brandId(2L).name("Honda").build();
        when(brandRepository.findAll()).thenReturn(List.of(b1, b2));

        List<Brand> result = service.getAll();

        assertEquals(2, result.size());
        assertSame(b1, result.get(0));
        assertSame(b2, result.get(1));
        verify(brandRepository).findAll();
    }

    @Test
    void getById_ShouldReturnBrand_WhenFound() {
        Brand b1 = Brand.builder().brandId(1L).name("Toyota").build();
        when(brandRepository.findById(1L)).thenReturn(Optional.of(b1));

        Brand result = service.getById(1L);

        assertSame(b1, result);
        verify(brandRepository).findById(1L);
    }

    @Test
    void getById_ShouldReturnNull_WhenNotFound() {
        when(brandRepository.findById(1L)).thenReturn(Optional.empty());

        Brand result = service.getById(1L);

        assertNull(result);
        verify(brandRepository).findById(1L);
    }
}


