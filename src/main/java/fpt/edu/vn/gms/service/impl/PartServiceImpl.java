package fpt.edu.vn.gms.service.impl;

import fpt.edu.vn.gms.entity.Part;
import fpt.edu.vn.gms.repository.PartRepository;
import fpt.edu.vn.gms.service.PartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PartServiceImpl implements PartService {

    private final PartRepository partRepository;

    @Override
    public List<Part> getAllPart() {
        return partRepository.findAll();
    }
}
