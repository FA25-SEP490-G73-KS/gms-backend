package fpt.edu.vn.gms.service.impl;

import fpt.edu.vn.gms.base.BaseServiceTest;
import fpt.edu.vn.gms.entity.ServiceType;
import fpt.edu.vn.gms.repository.ServiceTypeRepository;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ServiceTypeServiceImplTest extends BaseServiceTest {

  @Mock
  private ServiceTypeRepository repository;

  @InjectMocks
  private ServiceTypeServiceImpl serviceTypeServiceImpl;

  @Test
  void getAll_WhenServiceTypesExist_ShouldReturnServiceTypeList() {
    ServiceType st1 = ServiceType.builder().id(1L).name("Bảo dưỡng").build();
    ServiceType st2 = ServiceType.builder().id(2L).name("Sửa chữa").build();
    when(repository.findAll()).thenReturn(List.of(st1, st2));

    List<ServiceType> result = serviceTypeServiceImpl.getAll();

    assertEquals(2, result.size());
    assertEquals("Bảo dưỡng", result.get(0).getName());
    assertEquals("Sửa chữa", result.get(1).getName());
    verify(repository).findAll();
  }

  @Test
  void getAll_WhenNoServiceTypesExist_ShouldReturnEmptyList() {
    when(repository.findAll()).thenReturn(List.of());

    List<ServiceType> result = serviceTypeServiceImpl.getAll();

    assertNotNull(result);
    assertTrue(result.isEmpty());
    verify(repository).findAll();
  }
}
