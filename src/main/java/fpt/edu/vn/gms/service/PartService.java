package fpt.edu.vn.gms.service;

import fpt.edu.vn.gms.common.enums.StockLevelStatus;
import fpt.edu.vn.gms.dto.request.PartUpdateReqDto;
import fpt.edu.vn.gms.dto.response.PartReqDto;
import org.springframework.data.domain.Page;

public interface PartService {

    Page<PartReqDto> getAllPart(int page, int size);

    PartReqDto getPartById(Long id);

    PartReqDto createPart(PartUpdateReqDto part);

    Page<PartReqDto> getPartByCategory(Long categoryId, int page, int size);

    PartReqDto updatePart(Long id, PartUpdateReqDto dto);

    // New method to support filtering by category id and stock status
    Page<PartReqDto> getAllPart(int page, int size, Long categoryId, StockLevelStatus status);

    void deletePart(Long id);
}
