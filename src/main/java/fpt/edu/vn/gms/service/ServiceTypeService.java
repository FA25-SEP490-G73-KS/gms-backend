package fpt.edu.vn.gms.service;

import fpt.edu.vn.gms.dto.request.ServiceTypeCreateRequest;
import fpt.edu.vn.gms.entity.ServiceType;

import java.util.List;

public interface ServiceTypeService {

    List<ServiceType> getAll();

    ServiceType create(ServiceTypeCreateRequest request);

    void delete(Long id);
}
