package fpt.edu.vn.gms.service;

import fpt.edu.vn.gms.dto.request.ChangePasswordRequest;
import fpt.edu.vn.gms.dto.request.ResetPasswordRequestDto;
import fpt.edu.vn.gms.dto.response.AccountResponseDto;
import fpt.edu.vn.gms.dto.response.ResetPasswordResponseDto;

public interface AccountService {

   ResetPasswordResponseDto resetPassword(ResetPasswordRequestDto req);

   AccountResponseDto changePassword(String phoneNumber, ChangePasswordRequest req);
}
