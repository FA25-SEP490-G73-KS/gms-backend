package fpt.edu.vn.gms.service;

import fpt.edu.vn.gms.dto.request.ExpenseVoucherCreateRequest;
import fpt.edu.vn.gms.dto.request.ManualVoucherCreateRequest;
import fpt.edu.vn.gms.dto.response.ManualVoucherListResponseDto;
import fpt.edu.vn.gms.dto.response.ManualVoucherResponseDto;
import fpt.edu.vn.gms.entity.Employee;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

public interface ManualVoucherService {

    ManualVoucherResponseDto payForStockReceiptItem(Long itemId,
                                                    ExpenseVoucherCreateRequest request,
                                                    Employee accountant);

    ManualVoucherResponseDto create(ManualVoucherCreateRequest req, MultipartFile file, Employee creator);

    Page<ManualVoucherListResponseDto> getList(int page, int size);

    ManualVoucherResponseDto getDetail(Long id);

    ManualVoucherResponseDto approveVoucher(Long voucherId, Employee approver);

    ManualVoucherResponseDto finishVoucher(Long voucherId);
}
