package fpt.edu.vn.gms.service;

import com.google.firebase.auth.FirebaseAuthException;
import fpt.edu.vn.gms.dto.*;

public interface AccountService {

   ResetPasswordResponseDTO resetPassword(ResetPasswordRequestDTO resetPasswordRequestDTO) throws FirebaseAuthException;
}
