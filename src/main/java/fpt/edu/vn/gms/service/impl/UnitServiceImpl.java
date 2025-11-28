package fpt.edu.vn.gms.service.impl;

import fpt.edu.vn.gms.dto.request.UnitRequestDto;
import fpt.edu.vn.gms.dto.response.UnitResponseDto;
import fpt.edu.vn.gms.entity.Unit;
import fpt.edu.vn.gms.exception.ResourceNotFoundException;
import fpt.edu.vn.gms.mapper.UnitMapper;
import fpt.edu.vn.gms.repository.UnitRepository;
import fpt.edu.vn.gms.service.UnitService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UnitServiceImpl implements UnitService {

    private final UnitRepository unitRepository;
    private final UnitMapper unitMapper;

    @Override
    public Page<UnitResponseDto> getAll(Pageable pageable) {
        return unitRepository.findAll(pageable)
                .map(unitMapper::toResponseDto);
    }

    @Override
    public UnitResponseDto getById(Long id) {
        Unit unit = unitRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Không tìm thấy đơn vị với id: " + id)
                );

        return unitMapper.toResponseDto(unit);
    }

    @Override
    public UnitResponseDto create(UnitRequestDto dto) {
        Unit entity = Unit.builder()
                .name(dto.getName())
                .build();

        Unit saved = unitRepository.save(entity);

        return unitMapper.toResponseDto(saved);
    }

    @Override
    public UnitResponseDto update(Long id, UnitRequestDto dto) {
        Unit entity = unitRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Không tìm thấy đơn vị với id: " + id)
                );

        entity.setName(dto.getName());

        Unit updated = unitRepository.save(entity);

        return unitMapper.toResponseDto(updated);
    }

    @Override
    public void delete(Long id) {
        if (!unitRepository.existsById(id)) {
            throw new ResourceNotFoundException("Không tìm thấy đơn vị với id: " + id);
        }

        unitRepository.deleteById(id);
    }
}
