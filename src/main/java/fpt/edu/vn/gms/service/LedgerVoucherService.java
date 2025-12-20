package fpt.edu.vn.gms.service;

import fpt.edu.vn.gms.dto.request.ApproveVoucherRequest;
import fpt.edu.vn.gms.dto.request.CreateVoucherRequest;
import fpt.edu.vn.gms.dto.request.UpdateVoucherRequest;
import fpt.edu.vn.gms.dto.response.LedgerVoucherDetailResponse;
import fpt.edu.vn.gms.dto.response.LedgerVoucherListResponse;
import fpt.edu.vn.gms.entity.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface LedgerVoucherService {

    LedgerVoucherDetailResponse createManualVoucher(CreateVoucherRequest request, MultipartFile file, Employee currentUser);

    LedgerVoucherDetailResponse createPaymentVoucherFromReceiptHistory(Long receiptHistoryId,
            CreateVoucherRequest request, MultipartFile file);

    LedgerVoucherDetailResponse updateVoucher(Long id, UpdateVoucherRequest request);

    LedgerVoucherDetailResponse approveVoucher(Long id, ApproveVoucherRequest request);

    LedgerVoucherDetailResponse rejectVoucher(Long id);

    LedgerVoucherDetailResponse payVoucher(Long id);

    LedgerVoucherDetailResponse getVoucherDetail(Long id);

    Page<LedgerVoucherListResponse> getVoucherList(String keyword,
            String type,
            String status,
            String fromDate,
            String toDate,
            Long supplierId,
            Long employeeId,
            Pageable pageable);

    /**
     * Xóa phiếu thu/chi. Chỉ cho phép xóa khi trạng thái là PENDING (chờ duyệt).
     */
    void deleteVoucher(Long id);
}
