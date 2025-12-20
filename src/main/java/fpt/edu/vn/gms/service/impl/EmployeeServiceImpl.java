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
import fpt.edu.vn.gms.entity.Employee;
import fpt.edu.vn.gms.repository.AccountRepository;
import fpt.edu.vn.gms.repository.EmployeeRepository;
import fpt.edu.vn.gms.service.EmployeeService;
import fpt.edu.vn.gms.service.zalo.ZnsNotificationService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.security.SecureRandom;
import java.util.List;

@Service
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class EmployeeServiceImpl implements EmployeeService {

    EmployeeRepository employeeRepository;
    AccountRepository accountRepository;
    PasswordEncoder passwordEncoder;
    ZnsNotificationService znsNotificationService;

    @Override
    public List<EmployeeDto> findAllEmployeeIsTechniciansActive() {

        return employeeRepository.findAllEmployeeIsTechniciansActive();
    }

    @Override
    public EmployeeInfoResponseDto findEmployeeInfoByPhone(String phone) {

        return employeeRepository.findEmployeeInfoByPhone(phone);
    }

    @Override
    public Page<EmployeeListResponse> findAll(int page, int size, Boolean statusFilter) {

        Pageable pageable = Pageable.ofSize(size).withPage(page);
        Page<Employee> employeesPage = employeeRepository.findAll(pageable);

        List<Employee> employees = employeesPage.getContent();

        List<EmployeeListResponse> computedList = employees.stream()
                .map(emp -> {
                    return EmployeeListResponse.builder()
                            .employeeId(emp.getEmployeeId())
                            .fullName(emp.getFullName())
                            .phone(emp.getPhone())
                            .role(emp.getAccount() != null ? emp.getAccount().getRole() : Role.TECHNICIAN)
                            .hireDate(emp.getHireDate())
                            .dailySalary(emp.getDailySalary())
                            .status(emp.isActive())
                            .build();
                })
                .toList();

        List<EmployeeListResponse> filtered = statusFilter == null
                ? computedList
                : computedList.stream()
                        .filter(e -> e.isStatus() == statusFilter)
                        .toList();

        return new PageImpl<>(filtered, pageable, employeesPage.getTotalElements());
    }

    // private String computeStatus(Attendance attendance) {
    // if (attendance == null) {
    // return "Nghỉ làm";
    // }
    //
    // Boolean present = attendance.getIsPresent();
    // String note = attendance.getNote();
    //
    // if (Boolean.TRUE.equals(present)) {
    // return "Đang hoạt động";
    // }
    //
    // if (Boolean.FALSE.equals(present) && "leave".equalsIgnoreCase(note)) {
    // return "Nghỉ phép";
    // }
    //
    // // isPresent = false hoặc các trường hợp còn lại
    // return "Nghỉ làm";
    // }

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

        // Build address from parts
        String address = String.join(", ", request.getDetailAddress(), request.getWard(), request.getCity());

        Account account = null;
        String randomPassword = null;
        // Chỉ tạo Account nếu role không phải là TECHNICIAN
        if (role != Role.TECHNICIAN) {
            // Generate random password 6 characters
            randomPassword = generateRandomPassword(6);

            // Create Account with random password
            account = Account.builder()
                    .phone(request.getPhone())
                    .role(role)
                    .password(passwordEncoder.encode(randomPassword))
                    .active(true)
                    .build();
        }

        // Create Employee entity
        Employee employee = Employee.builder()
                .fullName(request.getFullName())
                .dateOfBirth(request.getDateOfBirth())
                .phone(request.getPhone())
                .address(address)
                .hireDate(request.getStartDate())
                .terminationDate(request.getEndDate())
                .dailySalary(request.getDailySalary())
                .isActive(true)
                .account(account)
                .build();

        // Set back-reference for bi-directional mapping if account exists
        if (account != null) {
            account.setEmployee(employee);
            // Persist account first (cascade or manual)
            accountRepository.save(account);
        }

        Employee saved = employeeRepository.save(employee);

        // Map to response DTO trước
        EmployeeResponse response = EmployeeResponse.builder()
                .id(saved.getEmployeeId())
                .fullName(saved.getFullName())
                .dateOfBirth(saved.getDateOfBirth())
                .phone(saved.getPhone())
                .address(saved.getAddress())
                .role(saved.getAccount() != null ? saved.getAccount().getRole() : null)
                .dailySalary(saved.getDailySalary())
                .hireDate(saved.getHireDate())
                .terminationDate(saved.getTerminationDate())
                .status(saved.isActive() ? "Active" : "Inactive")
                .build();

        // Gửi thông báo ZNS SAU KHI transaction commit (bên ngoài transaction)
        // để tránh xung đột khi generate UUID cho OneTimeToken
        if (account != null && randomPassword != null) {
            // Giữ số điện thoại ở format Việt Nam (0986475989) để truyền vào method
            // Method sendAccountInfoNotification sẽ tự normalize khi gửi qua API
            final String phone = request.getPhone();
            final String fullName = request.getFullName();
            final String password = randomPassword;

            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    try {
                        znsNotificationService.sendAccountInfoNotification(phone, fullName, password);
                        log.info("Đã gửi thông báo thông tin tài khoản qua ZNS cho nhân viên: {}", phone);
                    } catch (Exception e) {
                        log.error("Lỗi khi gửi thông báo thông tin tài khoản qua ZNS: {}", e.getMessage(), e);
                        // Không throw exception để không ảnh hưởng đến việc tạo employee
                    }
                }
            });
        }

        return response;
    }

    /**
     * Generate random password with specified length
     * 
     * @param length password length
     * @return random password string
     */
    private String generateRandomPassword(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            password.append(chars.charAt(random.nextInt(chars.length())));
        }

        return password.toString();
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
                .status(employee.isActive() ? "Active" : "Inactive")
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

        // Cập nhật role nếu position thay đổi
        if (employee.getAccount() != null) {
            Role currentRole = employee.getAccount().getRole();
            Role newRole = mapPositionToRole(request.getPosition());
            if (newRole != null && !newRole.equals(currentRole)) {
                employee.getAccount().setRole(newRole);
            }
        }

        System.out.println(request.isActive());
        employee.setActive(request.isActive());

        Employee saved = employeeRepository.save(employee);

        // Trả về detail giống getEmployeeDetail
        return getEmployeeDetail(saved.getEmployeeId());
    }

    private Role mapPositionToRole(String position) {
        if (position == null)
            return null;
        String normalized = position.trim().toLowerCase();
        for (Role role : Role.values()) {
            if (role.getValue().toLowerCase().equals(normalized) || role.name().toLowerCase().equals(normalized)) {
                return role;
            }
        }
        throw new IllegalArgumentException("Vị trí không hợp lệ: " + position);
    }

    @Override
    @Transactional
    public void updateEmployeeActiveStatus(Long id, boolean isActive) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy nhân viên"));
        employee.setActive(isActive);
        employeeRepository.save(employee);
    }
}
