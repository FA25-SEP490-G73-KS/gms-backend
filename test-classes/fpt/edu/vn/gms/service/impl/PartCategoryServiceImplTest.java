package fpt.edu.vn.gms.service.impl;

import fpt.edu.vn.gms.entity.PartCategory;
import fpt.edu.vn.gms.repository.PartCategoryRepository;
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
class PartCategoryServiceImplTest {

    @Mock
    PartCategoryRepository repository;

    @InjectMocks
    PartCategoryServiceImpl service;

    @Test
    void getAll_ShouldReturnAllCategories() {
        PartCategory c1 = PartCategory.builder().id(1L).name("Engine").build();
        PartCategory c2 = PartCategory.builder().id(2L).name("Brake").build();
        when(repository.findAll()).thenReturn(List.of(c1, c2));

        List<PartCategory> result = service.getAll();

        assertEquals(2, result.size());
        assertSame(c1, result.get(0));
        assertSame(c2, result.get(1));
        verify(repository).findAll();
    }

    @Test
    void getById_ShouldReturnCategory_WhenFound() {
        PartCategory c1 = PartCategory.builder().id(1L).name("Engine").build();
        when(repository.findById(1L)).thenReturn(Optional.of(c1));

        PartCategory result = service.getById(1L);

        assertSame(c1, result);
        verify(repository).findById(1L);
    }

    @Test
    void getById_ShouldReturnNull_WhenNotFound() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        PartCategory result = service.getById(1L);

        assertNull(result);
        verify(repository).findById(1L);
    }
}


