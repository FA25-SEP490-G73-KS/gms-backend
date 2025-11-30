package fpt.edu.vn.gms.service.impl;

import fpt.edu.vn.gms.base.BaseServiceTest;
import fpt.edu.vn.gms.entity.PartCategory;
import fpt.edu.vn.gms.repository.PartCategoryRepository;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PartCategoryServiceImplTest extends BaseServiceTest {

  @Mock
  private PartCategoryRepository repository;

  @InjectMocks
  private PartCategoryServiceImpl partCategoryServiceImpl;

  @Test
  void getAll_WhenCategoriesExist_ShouldReturnCategoryList() {
    PartCategory cat1 = PartCategory.builder().id(1L).name("Engine").build();
    PartCategory cat2 = PartCategory.builder().id(2L).name("Body").build();
    when(repository.findAll()).thenReturn(List.of(cat1, cat2));

    List<PartCategory> result = partCategoryServiceImpl.getAll();

    assertEquals(2, result.size());
    assertEquals("Engine", result.get(0).getName());
    assertEquals("Body", result.get(1).getName());
    verify(repository).findAll();
  }

  @Test
  void getAll_WhenNoCategoriesExist_ShouldReturnEmptyList() {
    when(repository.findAll()).thenReturn(List.of());

    List<PartCategory> result = partCategoryServiceImpl.getAll();

    assertNotNull(result);
    assertTrue(result.isEmpty());
    verify(repository).findAll();
  }
}
