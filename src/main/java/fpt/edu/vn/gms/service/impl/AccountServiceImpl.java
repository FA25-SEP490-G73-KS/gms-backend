package fpt.edu.vn.gms.service.impl;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import fpt.edu.vn.gms.dto.request.ChangePasswordRequest;
import fpt.edu.vn.gms.dto.request.ResetPasswordRequestDto;
import fpt.edu.vn.gms.dto.response.AccountResponseDto;
import fpt.edu.vn.gms.dto.response.ResetPasswordResponseDto;
import fpt.edu.vn.gms.entity.Account;
import fpt.edu.vn.gms.exception.ResourceNotFoundException;
import fpt.edu.vn.gms.mapper.AccountMapper;
import fpt.edu.vn.gms.repository.AccountRepository;
import fpt.edu.vn.gms.service.AccountService;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepo;
    private final PasswordEncoder passwordEncoder;
    private AccountMapper accountMapper;

    @Override
    public ResetPasswordResponseDto resetPassword(ResetPasswordRequestDto resetPasswordRequestDTO) {

        Account account = accountRepo.findByPhone(resetPasswordRequestDTO.getPhone())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng"));

        String newPassword = generateRandomPassword();

        account.setPassword(passwordEncoder.encode(newPassword));
        accountRepo.save(account);

        System.out.println("Gửi SMS đến " + account.getPhone() + " Mật khẩu mới là: " + newPassword);

        return ResetPasswordResponseDto.builder()
                .phone(account.getPhone())
                .message("Mật khẩu mới đã được gửi qua SMS")
                .build();
    }


    @Override
    public AccountResponseDto changePassword(String phoneNumber, ChangePasswordRequest req) {

        Account acc = accountRepo.findByPhone(phoneNumber)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng!"));

        System.out.println(req.getNewPassword());
        System.out.println(acc.getPassword());
        if (!passwordEncoder.matches(req.getCurrentPassword(), acc.getPassword())) {
            throw new RuntimeException("Mật khẩu hiện tại không đúng!");
        }

        acc.setPassword(passwordEncoder.encode(req.getNewPassword()));
        accountRepo.save(acc);
        return accountMapper.toDTO(acc);
    }

    private String generateRandomPassword() {
        return UUID.randomUUID().toString().substring(0, 8); // random 8 ký tự
    }
}
