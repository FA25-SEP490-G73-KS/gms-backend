package fpt.edu.vn.gms.service;

import fpt.edu.vn.gms.dto.request.PartReqDto;
import fpt.edu.vn.gms.dto.response.PartResDto;
import fpt.edu.vn.gms.entity.Part;
import org.springframework.data.domain.Page;

import java.util.List;

public interface PartService {

    Page<PartResDto> getAllPart(int page, int size);

    PartResDto createPart(PartReqDto part);

    Page<PartResDto> getPartByCategory(String categoryName, int page, int size);
}
