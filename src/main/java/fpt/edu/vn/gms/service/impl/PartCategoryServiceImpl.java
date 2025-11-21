package fpt.edu.vn.gms.service.impl;

import fpt.edu.vn.gms.entity.PartCategory;
import fpt.edu.vn.gms.repository.PartCategoryRepository;
import fpt.edu.vn.gms.service.PartCategoryService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PartCategoryServiceImpl implements PartCategoryService {

    PartCategoryRepository repository;

    @Override
    public List<PartCategory> getAll() {
        return repository.findAll();
    }
}
