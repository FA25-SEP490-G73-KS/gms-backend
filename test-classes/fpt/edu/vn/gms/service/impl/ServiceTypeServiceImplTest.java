package fpt.edu.vn.gms.service.impl;

import fpt.edu.vn.gms.entity.ServiceType;
import fpt.edu.vn.gms.repository.ServiceTypeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ServiceTypeServiceImplTest {

    @Mock
    ServiceTypeRepository repository;

    @InjectMocks
    ServiceTypeServiceImpl service;

    @Test
    void getAll_ShouldReturnAllFromRepository() {
        List<ServiceType> list = List.of(
                ServiceType.builder().id(1L).name("Rửa xe").build()
        );
        when(repository.findAll()).thenReturn(list);

        List<ServiceType> result = service.getAll();

        assertSame(list, result);
        verify(repository).findAll();
    }
}


