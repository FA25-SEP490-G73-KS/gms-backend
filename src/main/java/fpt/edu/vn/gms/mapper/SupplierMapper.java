package fpt.edu.vn.gms.mapper;

import fpt.edu.vn.gms.dto.request.SupplierRequestDto;
import fpt.edu.vn.gms.dto.response.SupplierResponseDto;
import fpt.edu.vn.gms.entity.Supplier;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface SupplierMapper {
    SupplierResponseDto toResponseDto(Supplier supplier);
    Supplier toEntity(SupplierRequestDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateSupplierFromDto(SupplierRequestDto dto, @MappingTarget Supplier entity);
}

