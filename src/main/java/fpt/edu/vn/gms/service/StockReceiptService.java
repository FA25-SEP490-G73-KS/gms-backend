package fpt.edu.vn.gms.service;

import fpt.edu.vn.gms.dto.request.StockReceiveRequest;
import fpt.edu.vn.gms.dto.response.StockReceiptItemResponseDto;
import fpt.edu.vn.gms.dto.response.StockReceiptResponseDto;
import fpt.edu.vn.gms.entity.Employee;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface StockReceiptService {

    StockReceiptItemResponseDto receiveItem(Long prItemId, StockReceiveRequest req, MultipartFile file, Employee employee);

    // Kế toán
    Page<StockReceiptResponseDto> getReceiptsForAccounting(int page, int size, String search);

    // Danh sách dòng nhập kho theo phiếu - phân trang
    Page<StockReceiptItemResponseDto> getReceiptItems(Long receiptId, int page, int size);

    // Lấy chi tiết 1 dòng nhập kho trong 1 phiếu (đảm bảo thuộc đúng receipt)
    StockReceiptItemResponseDto getReceiptItemDetail(Long itemId);
}
