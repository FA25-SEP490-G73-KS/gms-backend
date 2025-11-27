package fpt.edu.vn.gms.service;

import fpt.edu.vn.gms.dto.request.UnitRequestDto;
import fpt.edu.vn.gms.dto.response.UnitResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface UnitService {

    Page<UnitResponseDto> getAll(Pageable pageable);

    UnitResponseDto getById(Long id);

    UnitResponseDto create(UnitRequestDto dto);

    UnitResponseDto update(Long id, UnitRequestDto dto);

    void delete(Long id);
}
