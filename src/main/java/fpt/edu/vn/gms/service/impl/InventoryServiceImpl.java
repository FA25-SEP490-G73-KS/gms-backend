package fpt.edu.vn.gms.service.impl;

import fpt.edu.vn.gms.entity.Part;
import fpt.edu.vn.gms.repository.PartRepository;
import fpt.edu.vn.gms.service.InventoryService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class InventoryServiceImpl implements InventoryService {

    PartRepository partRepository;

    @Override
    public double getAvailableQuantity(Long partId) {
        Part part = partRepository.findById(partId).orElse(null);
        if (part == null) return 0.0;
        double inStock = part.getQuantityInStock() != null ? part.getQuantityInStock() : 0.0;
        return inStock;
    }

    @Override
    public double getReservedQuantity(Long partId) {
        Part part = partRepository.findById(partId).orElse(null);
        if (part == null) return 0.0;
        return part.getReservedQuantity() != null ? part.getReservedQuantity() : 0.0;
    }
}

