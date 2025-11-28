package fpt.edu.vn.gms.base;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import fpt.edu.vn.gms.common.enums.Role;
import fpt.edu.vn.gms.entity.Account;
import fpt.edu.vn.gms.entity.Employee;

@ExtendWith(MockitoExtension.class)
@Tag("unit")
public class BaseServiceTest {

  protected Account getMockAccount(Role role) {
    String phone = "0123456789";
    String password = "123456";

    Account account = Account.builder()
        .phone(phone)
        .password(password)
        .role(role)
        .active(true)
        .build();
    return account;
  }

  protected Employee getMockEmployee(Role role) {
    Account mockAccount = getMockAccount(role);

    return Employee.builder()
        .employeeId(1L)
        .account(mockAccount)
        .fullName("John Doe")
        .gender("male")
        .dateOfBirth(LocalDate.now())
        .phone(mockAccount.getPhone())
        .address("123 Street")
        .hireDate(LocalDateTime.now())
        .dailySalary(new BigDecimal(10000))
        .status("Active")
        .build();
  }

  protected Employee getMockTechnician() {
    return Employee.builder()
        .employeeId(1L)
        .account(null)
        .fullName("John Doe")
        .gender("male")
        .dateOfBirth(LocalDate.now())
        .phone("0123456789")
        .address("123 Street")
        .hireDate(LocalDateTime.now())
        .dailySalary(new BigDecimal(10000))
        .status("Active")
        .build();

  }
}
