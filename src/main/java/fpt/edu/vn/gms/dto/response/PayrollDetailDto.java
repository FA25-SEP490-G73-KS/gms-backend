package fpt.edu.vn.gms.dto.response;

import fpt.edu.vn.gms.common.enums.PayrollStatus;
import fpt.edu.vn.gms.dto.request.DeductionDto;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class PayrollDetailDto {

    private EmployeeInfoDto employee;

    private PayrollOverviewDto overview;

    private List<AllowanceDto> allowances;
    private List<DeductionDto> deductions;

    private Integer workingDays;
    private Integer leaveDays;

    private PayrollStatus status;   // Chờ duyệt / Đang xử lý / Đã chi trả
    private boolean canPaySalary;   // bật nút “Chi lương”
}

