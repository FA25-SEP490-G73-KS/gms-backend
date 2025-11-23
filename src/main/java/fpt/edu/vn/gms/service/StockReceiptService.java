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

    List<StockReceiptItemResponseDto> getReceiptItems(Long receiptId);
}
