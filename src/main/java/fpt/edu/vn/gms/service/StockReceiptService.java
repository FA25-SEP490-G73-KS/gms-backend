package fpt.edu.vn.gms.service;

import fpt.edu.vn.gms.dto.request.StockReceiveRequest;
import fpt.edu.vn.gms.dto.response.StockReceiptItemResponseDto;
import fpt.edu.vn.gms.entity.Employee;
import org.springframework.web.multipart.MultipartFile;

public interface StockReceiptService {

    StockReceiptItemResponseDto receiveItem(Long prItemId, StockReceiveRequest req, MultipartFile file, Employee employee);
}
