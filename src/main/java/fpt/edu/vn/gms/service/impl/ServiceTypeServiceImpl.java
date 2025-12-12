package fpt.edu.vn.gms.service.impl;

import fpt.edu.vn.gms.dto.request.ServiceTypeCreateRequest;
import fpt.edu.vn.gms.entity.ServiceType;
import fpt.edu.vn.gms.exception.ResourceNotFoundException;
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

    @Override
    public ServiceType create(ServiceTypeCreateRequest request) {
        ServiceType serviceType = ServiceType.builder()
                .name(request.getName())
                .build();
        return repository.save(serviceType);
    }

    @Override
    public void delete(Long id) {
        ServiceType serviceType = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy loại dịch vụ"));
        repository.delete(serviceType);
    }
}
