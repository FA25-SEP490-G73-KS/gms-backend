package fpt.edu.vn.gms.service.impl;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import fpt.edu.vn.gms.dto.request.ResetPasswordRequestDTO;
import fpt.edu.vn.gms.dto.response.ResetPasswordResponseDTO;
import fpt.edu.vn.gms.entity.Account;
import fpt.edu.vn.gms.mapper.AccountMapper;
import fpt.edu.vn.gms.repository.AccountRepository;
import fpt.edu.vn.gms.service.AccountService;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepo;
    private final PasswordEncoder passwordEncoder;

    @Override
    public ResetPasswordResponseDTO resetPassword(ResetPasswordRequestDTO resetPasswordRequestDTO) throws FirebaseAuthException {

        FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(resetPasswordRequestDTO.getIdToken());

        // Get phoneNumber từ UserRecord
        String uid = decodedToken.getUid();
        String phoneNumber = FirebaseAuth.getInstance().getUser(uid).getPhoneNumber();

        if (phoneNumber == null) {
            throw new IllegalArgumentException("Token không chứa số điện thoại!!");
        }

        Account account = accountRepo.findByPhone(phoneNumber)
                .orElseThrow(() -> new RuntimeException("User không tồn tại!"));

        account.setPassword(passwordEncoder.encode(resetPasswordRequestDTO.getNewPassword()));
        account = accountRepo.save(account);

        ResetPasswordResponseDTO newPasswordDTO = AccountMapper.INSTANCE.toResetPasswordResponseDTO(account);
        newPasswordDTO.setMessage("Password cập nhật thành công!");
        return newPasswordDTO;
    }
}
