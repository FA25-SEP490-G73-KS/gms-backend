package fpt.edu.vn.gms.service;

import fpt.edu.vn.gms.dto.request.PartUpdateReqDto;
import fpt.edu.vn.gms.dto.response.PartReqDto;
import fpt.edu.vn.gms.dto.request.PartResDto;
import fpt.edu.vn.gms.entity.Part;
import org.springframework.data.domain.Page;

public interface PartService {

    Page<PartReqDto> getAllPart(int page, int size);

    PartReqDto getPartById(Long id);

    PartReqDto createPart(PartResDto part);

    Page<PartReqDto> getPartByCategory(String categoryName, int page, int size);

    PartReqDto updatePart(Long id, PartUpdateReqDto dto);
}
