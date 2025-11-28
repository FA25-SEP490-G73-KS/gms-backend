package fpt.edu.vn.gms.mapper;

import fpt.edu.vn.gms.dto.response.AttendanceResponseDTO;
import fpt.edu.vn.gms.entity.Attendance;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface AttendanceMapper {
  @Mapping(target = "employeeId", source = "employee.employeeId")
  @Mapping(target = "employeeName", source = "employee.fullName")
  @Mapping(target = "employeePhone", source = "employee.phone")
  @Mapping(target = "employeeRole", source = "employee.account.role")
  AttendanceResponseDTO toResponseDTO(Attendance attendance);
}
