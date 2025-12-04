package fpt.edu.vn.gms.service;

import fpt.edu.vn.gms.dto.response.StockReceiptItemHistoryListResponse;
import fpt.edu.vn.gms.dto.response.StockReceiptItemHistoryPaymentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface StockReceiptItemHistoryService {

    Page<StockReceiptItemHistoryListResponse> getHistories(String keyword,
                                                           Long supplierId,
                                                           String fromDate,
                                                           String toDate,
                                                           Pageable pageable);

    StockReceiptItemHistoryPaymentResponse getPaymentDetail(Long historyId);
}