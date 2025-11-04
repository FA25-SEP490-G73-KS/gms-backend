package fpt.edu.vn.gms.service;

import com.google.firebase.auth.FirebaseAuthException;
import fpt.edu.vn.gms.dto.request.ChangePasswordRequest;
import fpt.edu.vn.gms.dto.request.ResetPasswordRequestDTO;
import fpt.edu.vn.gms.dto.response.AccountResponseDto;
import fpt.edu.vn.gms.dto.response.ResetPasswordResponseDTO;

public interface AccountService {

   ResetPasswordResponseDTO resetPassword(ResetPasswordRequestDTO resetPasswordRequestDTO) throws FirebaseAuthException;

   AccountResponseDto changePassword(String phoneNumber, ChangePasswordRequest req);
}
