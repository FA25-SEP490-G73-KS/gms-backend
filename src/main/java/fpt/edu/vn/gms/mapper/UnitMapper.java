package fpt.edu.vn.gms.mapper;

import fpt.edu.vn.gms.dto.request.UnitRequestDto;
import fpt.edu.vn.gms.dto.response.UnitResponseDto;
import fpt.edu.vn.gms.entity.Unit;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UnitMapper {

    UnitResponseDto toResponseDto(Unit unit);
}
