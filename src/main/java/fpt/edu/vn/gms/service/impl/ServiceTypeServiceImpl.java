package fpt.edu.vn.gms.service.impl;

import fpt.edu.vn.gms.entity.ServiceType;
import fpt.edu.vn.gms.repository.ServiceTypeRepository;
import fpt.edu.vn.gms.service.ServiceTypeService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class ServiceTypeServiceImpl implements ServiceTypeService {

    ServiceTypeRepository repository;

    @Override
    public List<ServiceType> getAll() {
        return repository.findAll();
    }
}
