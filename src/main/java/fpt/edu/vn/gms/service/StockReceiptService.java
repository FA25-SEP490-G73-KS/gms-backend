package fpt.edu.vn.gms.service;

import fpt.edu.vn.gms.dto.response.StockReceiptItemResponseDto;
import fpt.edu.vn.gms.entity.Employee;

public interface StockReceiptService {

    StockReceiptItemResponseDto receiveItem(Long prItemId, Employee employee);

}
