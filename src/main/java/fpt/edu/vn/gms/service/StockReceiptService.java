package fpt.edu.vn.gms.service;

import fpt.edu.vn.gms.dto.response.StockReceiptItemResponseDto;

public interface StockReceiptService {

    StockReceiptItemResponseDto receiveItem(Long prItemId);

}
