package fpt.edu.vn.gms.service;

import fpt.edu.vn.gms.dto.request.ExpenseVoucherCreateRequest;
import fpt.edu.vn.gms.dto.response.ExpenseVoucherResponseDto;
import fpt.edu.vn.gms.entity.Employee;

public interface ExpenseVoucherService {

    ExpenseVoucherResponseDto payForStockReceiptItem(Long itemId,
                                                     ExpenseVoucherCreateRequest request,
                                                     Employee accountant);
}
