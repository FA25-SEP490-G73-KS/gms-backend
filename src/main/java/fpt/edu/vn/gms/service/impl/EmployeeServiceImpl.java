package fpt.edu.vn.gms.service.impl;

import fpt.edu.vn.gms.common.enums.Role;
import fpt.edu.vn.gms.dto.EmployeeDto;
import fpt.edu.vn.gms.dto.request.EmployeeCreateRequest;
import fpt.edu.vn.gms.dto.request.EmployeeUpdateRequest;
import fpt.edu.vn.gms.dto.response.EmployeeDetailResponse;
import fpt.edu.vn.gms.dto.response.EmployeeInfoResponseDto;
import fpt.edu.vn.gms.dto.response.EmployeeListResponse;
import fpt.edu.vn.gms.dto.response.EmployeeResponse;
import fpt.edu.vn.gms.entity.Account;
import fpt.edu.vn.gms.entity.Attendance;
import fpt.edu.vn.gms.entity.Employee;
import fpt.edu.vn.gms.mapper.EmployeeMapper;
import fpt.edu.vn.gms.repository.AccountRepository;
import fpt.edu.vn.gms.repository.AttendanceRepository;
import fpt.edu.vn.gms.repository.EmployeeRepository;
import fpt.edu.vn.gms.service.EmployeeService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EmployeeServiceImpl implements EmployeeService {

    EmployeeRepository employeeRepository;
    EmployeeMapper employeeMapper;
    AccountRepository accountRepository;
    PasswordEncoder passwordEncoder;
    AttendanceRepository attendanceRepository;

    @Override
    public List<EmployeeDto> findAllEmployeeIsTechniciansActive() {

        return employeeRepository.findAllEmployeeIsTechniciansActive();
    }

    @Override
    public EmployeeInfoResponseDto findEmployeeInfoByPhone(String phone) {

        return employeeRepository.findEmployeeInfoByPhone(phone);
    }

    @Override
    public Page<EmployeeListResponse> findAll(int page, int size, String statusFilter) {

        Pageable pageable = Pageable.ofSize(size).withPage(page);
        Page<Employee> employeesPage = employeeRepository.findAll(pageable);

        List<Employee> employees = employeesPage.getContent();
        List<Long> employeeIds = employees.stream()
                .map(Employee::getEmployeeId)
                .toList();

        LocalDate today = LocalDate.now();
        Map<Long, Attendance> todayAttendanceMap = attendanceRepository.findTodayAttendance(today, employeeIds)
                .stream()
                .collect(Collectors.toMap(a -> a.getEmployee().getEmployeeId(), Function.identity()));

        List<EmployeeListResponse> computedList = employees.stream()
                .map(emp -> {
                    Attendance att = todayAttendanceMap.get(emp.getEmployeeId());
                    String computedStatus = computeStatus(att);
                    return EmployeeListResponse.builder()
                            .employeeId(emp.getEmployeeId())
                            .fullName(emp.getFullName())
                            .phone(emp.getPhone())
                            .role(emp.getAccount() != null ? emp.getAccount().getRole() : Role.WAREHOUSE)
                            .hireDate(emp.getHireDate())
                            .dailySalary(emp.getDailySalary())
                            .status(computedStatus)
                            .build();
                })
                .toList();

        // Apply status filter on computed status if provided
        List<EmployeeListResponse> filtered;
        if (statusFilter != null && !statusFilter.isBlank()) {
            filtered = computedList.stream()
                    .filter(e -> statusFilter.equalsIgnoreCase(e.getStatus()))
                    .toList();
        } else {
            filtered = computedList;
        }

        return new PageImpl<>(filtered, pageable, employeesPage.getTotalElements());
    }

    private String computeStatus(Attendance attendance) {
        if (attendance == null) {
            return "Nghỉ làm";
        }

        Boolean present = attendance.getIsPresent();
        String note = attendance.getNote();

        if (Boolean.TRUE.equals(present)) {
            return "Đang hoạt động";
        }

        if (Boolean.FALSE.equals(present) && "leave".equalsIgnoreCase(note)) {
            return "Nghỉ phép";
        }

        // isPresent = false hoặc các trường hợp còn lại
        return "Nghỉ làm";
    }

    @Override
    @Transactional
    public EmployeeResponse createEmployee(EmployeeCreateRequest request) {
        // Check duplicate phone by Employee and Account
        if (employeeRepository.findByPhone(request.getPhone()) != null) {
            throw new IllegalArgumentException("Số điện thoại đã tồn tại cho nhân viên khác");
        }

        if (accountRepository.findByPhone(request.getPhone()).isPresent()) {
            throw new IllegalArgumentException("Số điện thoại đã tồn tại cho tài khoản khác");
        }

        // Parse role from string
        Role role = Role.valueOf(request.getRole());

        // Create Account with default password 123456
        Account account = Account.builder()
                .phone(request.getPhone())
                .role(role)
                .password(passwordEncoder.encode("123456"))
                .active(true)
                .build();

        // Build address from parts
        String address = String.join(", ", request.getDetailAddress(), request.getWard(), request.getCity());

        // Create Employee entity
        Employee employee = Employee.builder()
                .fullName(request.getFullName())
                .gender(request.getGender())
                .dateOfBirth(request.getDateOfBirth())
                .phone(request.getPhone())
                .address(address)
                .hireDate(request.getStartDate())
                .terminationDate(request.getEndDate())
                .dailySalary(request.getDailySalary())
                .status("Active")
                .account(account)
                .build();

        // Set back-reference for bi-directional mapping if needed
        account.setEmployee(employee);

        // Persist account first (cascade or manual)
        accountRepository.save(account);

        Employee saved = employeeRepository.save(employee);

        // Map to response DTO
        return EmployeeResponse.builder()
                .id(saved.getEmployeeId())
                .fullName(saved.getFullName())
                .gender(saved.getGender())
                .dateOfBirth(saved.getDateOfBirth())
                .phone(saved.getPhone())
                .address(saved.getAddress())
                .role(saved.getAccount() != null ? saved.getAccount().getRole() : null)
                .dailySalary(saved.getDailySalary())
                .hireDate(saved.getHireDate())
                .terminationDate(saved.getTerminationDate())
                .status(saved.getStatus())
                .build();
    }

    @Override
    public EmployeeDetailResponse getEmployeeDetail(Long id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy nhân viên"));

        // Tách address: addressDetail, ward, province
        String address = employee.getAddress();
        String addressDetail = null;
        String ward = null;
        String province = null;
        if (address != null && !address.isBlank()) {
            String[] parts = address.split(",");
            if (parts.length >= 3) {
                addressDetail = parts[0].trim();
                ward = parts[1].trim();
                province = parts[2].trim();
            } else {
                addressDetail = address;
            }
        }

        String position = null;
        if (employee.getAccount() != null && employee.getAccount().getRole() != null) {
            position = employee.getAccount().getRole().getValue();
        }

        return EmployeeDetailResponse.builder()
                .employeeId(employee.getEmployeeId())
                .fullName(employee.getFullName())
                .phone(employee.getPhone())
                .province(province)
                .ward(ward)
                .addressDetail(addressDetail)
                .position(position)
                .dailySalary(employee.getDailySalary())
                .hireDate(employee.getHireDate())
                .terminationDate(employee.getTerminationDate())
                .status(employee.getStatus())
                .build();
    }

    @Override
    @Transactional
    public EmployeeDetailResponse updateEmployee(Long id, EmployeeUpdateRequest request) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy nhân viên"));

        // Cập nhật thông tin cơ bản
        employee.setFullName(request.getFullName());
        employee.setPhone(request.getPhone());

        // Ghép lại address từ province, ward, addressDetail
        String address = String.join(", ", request.getAddressDetail(), request.getWard(), request.getProvince());
        employee.setAddress(address);

        employee.setDailySalary(request.getDailySalary());
        employee.setHireDate(request.getHireDate());
        employee.setTerminationDate(request.getTerminationDate());
        employee.setStatus(request.getStatus());

        // Cập nhật role nếu position thay đổi
        if (employee.getAccount() != null) {
            Role currentRole = employee.getAccount().getRole();
            Role newRole = mapPositionToRole(request.getPosition());
            if (newRole != null && !newRole.equals(currentRole)) {
                employee.getAccount().setRole(newRole);
            }
        }

        Employee saved = employeeRepository.save(employee);

        // Trả về detail giống getEmployeeDetail
        return getEmployeeDetail(saved.getEmployeeId());
    }

    private Role mapPositionToRole(String position) {
        if (position == null) return null;
        String normalized = position.trim().toLowerCase();
        for (Role role : Role.values()) {
            if (role.getValue().toLowerCase().equals(normalized) || role.name().toLowerCase().equals(normalized)) {
                return role;
            }
        }
        throw new IllegalArgumentException("Vị trí không hợp lệ: " + position);
    }
}
