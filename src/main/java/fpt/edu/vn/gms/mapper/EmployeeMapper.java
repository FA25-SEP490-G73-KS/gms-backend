package fpt.edu.vn.gms.mapper;

import fpt.edu.vn.gms.dto.EmployeeDto;
import fpt.edu.vn.gms.entity.Employee;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {

    EmployeeDto toDto(Employee employee);
}
