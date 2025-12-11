package fpt.edu.vn.gms.controller;

import fpt.edu.vn.gms.common.annotations.CurrentUser;
import fpt.edu.vn.gms.dto.response.ApiResponse;
import fpt.edu.vn.gms.dto.response.PayrollDetailDto;
import fpt.edu.vn.gms.dto.response.PayrollExistsResponse;
import fpt.edu.vn.gms.dto.response.PayrollListItemDto;
import fpt.edu.vn.gms.dto.response.PayrollMonthlySummaryDto;
import fpt.edu.vn.gms.dto.response.PayrollSummaryDto;
import fpt.edu.vn.gms.entity.Employee;
import fpt.edu.vn.gms.service.PayrollService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payroll")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Tag(name = "payroll-controller", description = "API quản lý lương nhân viên")
public class PayrollController {

        private final PayrollService payrollService;

        @Operation(summary = "Xem bảng lương tạm tính", description = """
                        Lấy danh sách lương của từng nhân viên trong tháng.

                        Dữ liệu được tính động từ:
                        - Attendance (điểm danh)
                        - Allowance (phụ cấp)
                        - Deduction (khấu trừ)
                        - ManualVoucher (ứng lương)

                        Trường hợp payroll chưa được tạo, trạng thái = Chờ duyệt.
                        """)
        @ApiResponses({
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lấy bảng lương tạm tính thành công")
        })
        @GetMapping("/preview")
        public ResponseEntity<ApiResponse<PayrollMonthlySummaryDto>> getPreview(
                        @Parameter(description = "Tháng cần xem lương", example = "1") @RequestParam Integer month,

                        @Parameter(description = "Năm cần xem lương", example = "2025") @RequestParam Integer year) {

                return ResponseEntity.ok(
                                ApiResponse.success("Lương nhân viên tháng " + month,
                                                payrollService.getPayrollPreview(month, year)));
        }

        @Operation(summary = "Nộp bảng lương tháng", description = """
                        Kế toán nộp bảng lương để tạo Payroll.

                        Sau khi nộp:
                        - Payroll sẽ được lưu vào database (snapshot)
                        - Status = PENDING_MANAGER_APPROVAL (Chờ duyệt)

                        Không thể nộp bảng lương nếu payroll tháng đó đã tồn tại.
                        """)
        @PostMapping("/submit")
        public ResponseEntity<ApiResponse<Void>> submitPayroll(
                        @Parameter(description = "Tháng muốn nộp", example = "1") @RequestParam Integer month,

                        @Parameter(description = "Năm muốn nộp", example = "2025") @RequestParam Integer year,

                        @Parameter(description = "ID kế toán nộp bảng lương", example = "10") @CurrentUser Employee accountant) {

                Long accountantId = accountant.getEmployeeId();

                payrollService.submitPayroll(month, year, accountantId);
                return ResponseEntity.ok(ApiResponse.success("Nộp bảng lương thành công", null));
        }

        @Operation(summary = "Quản lý duyệt bảng lương", description = """
                        Quản lý duyệt bảng lương của một nhân viên.

                        Sau khi duyệt:
                        - Payroll status = APPROVED
                        - payroll.approvedBy = managerId
                        - payroll.approvedAt = now()
                        """)
        @PostMapping("/{id}/approve")
        public ResponseEntity<ApiResponse<Void>> approve(
                        @Parameter(description = "ID payroll cần duyệt", example = "123") @PathVariable Long id,

                        @Parameter(description = "ID quản lý duyệt", example = "5") @CurrentUser Employee manager) {

                Long managerId = manager.getEmployeeId();

                payrollService.approvePayroll(id, managerId);
                return ResponseEntity.ok(ApiResponse.success("Duyệt bảng lương thành công", null));
        }

        @Operation(summary = "Chi lương cho nhân viên", description = """
                        Tạo phiếu chi lương (ManualVoucher)

                        Điều kiện:
                        - Payroll phải được APPROVED bởi quản lý

                        Sau khi chi:
                        - Tạo phiếu chi loại SALARY_PAYMENT
                        - Payroll status = PAID
                        """)
        @PostMapping("/{id}/pay")
        public ResponseEntity<ApiResponse<Void>> pay(
                        @Parameter(description = "ID payroll cần chi lương", example = "123") @PathVariable Long id,

                        @Parameter(description = "ID kế toán thực hiện chi lương", example = "10") @CurrentUser Employee accountant) {

                Long accountantId = accountant.getEmployeeId();

                payrollService.createSalaryPaymentVoucher(id, accountantId);
                return ResponseEntity.ok(ApiResponse.success("Chi lương thành công", null));
        }

        @Operation(summary = "Xem chi tiết lương nhân viên", description = """
                        Trả về đầy đủ thông tin lương:
                        - Lương cơ bản
                        - Tổng công
                        - Nghỉ phép
                        - Tổng phụ cấp
                        - Tổng khấu trừ
                        - Lương ròng
                        - Danh sách phụ cấp
                        - Danh sách khấu trừ
                        - Trạng thái payroll
                        - Cho phép chi lương hay chưa
                        """)
        @GetMapping("/detail")
        public ResponseEntity<ApiResponse<PayrollDetailDto>> getPayrollDetail(
                        @RequestParam Long employeeId,
                        @RequestParam Integer month,
                        @RequestParam Integer year) {

                return ResponseEntity.ok(
                                ApiResponse.success(
                                                "Chi tiết lương nhân viên",
                                                payrollService.getPayrollDetail(employeeId, month, year)));
        }

        @Operation(summary = "Tổng hợp lương theo tháng/năm", description = "Lấy tổng hợp lương, phụ cấp, khấu trừ của toàn bộ nhân viên theo tháng và năm.")
        @GetMapping("/summary-by-month")
        public ResponseEntity<ApiResponse<PayrollSummaryDto>> getPayrollSummaryByMonthYear(@RequestParam Integer month,
                        @RequestParam Integer year) {
                PayrollSummaryDto summary = payrollService.getPayrollSummaryByMonthYear(month, year);
                return ResponseEntity.ok(ApiResponse.success("Tổng hợp lương toàn hệ thống theo tháng", summary));
        }

        @Operation(summary = "Lấy danh sách payroll", description = "Lấy danh sách payroll đã được tạo theo tháng và năm.")
        @ApiResponses({
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Lấy danh sách payroll thành công")
        })
        @GetMapping("/list")
        public ResponseEntity<ApiResponse<java.util.List<PayrollListItemDto>>> getPayrollList(
                        @Parameter(description = "Tháng cần xem", example = "1") @RequestParam Integer month,
                        @Parameter(description = "Năm cần xem", example = "2025") @RequestParam Integer year) {
                java.util.List<PayrollListItemDto> result = payrollService.getPayrollList(month, year);
                return ResponseEntity.ok(ApiResponse.success("Lấy danh sách payroll thành công", result));
        }

        @Operation(summary = "Kiểm tra payroll tồn tại", description = "Kiểm tra xem tháng/năm đó đã có payroll trong database chưa.")
        @ApiResponses({
                        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Kiểm tra thành công")
        })
        @GetMapping("/check-exists")
        public ResponseEntity<ApiResponse<PayrollExistsResponse>> checkPayrollExists(
                        @Parameter(description = "Tháng cần kiểm tra", example = "1") @RequestParam Integer month,
                        @Parameter(description = "Năm cần kiểm tra", example = "2025") @RequestParam Integer year) {
                PayrollExistsResponse result = payrollService.checkPayrollExists(month, year);
                return ResponseEntity.ok(ApiResponse.success("Kiểm tra payroll tồn tại thành công", result));
        }

}
