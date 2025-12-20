package fpt.edu.vn.gms.service;

import fpt.edu.vn.gms.dto.request.CreateReceiptItemHistoryRequest;
import fpt.edu.vn.gms.dto.response.StockReceiptDetailResponse;
import fpt.edu.vn.gms.dto.response.StockReceiptItemDetailResponse;
import fpt.edu.vn.gms.dto.response.StockReceiptItemHistoryDetailResponse;
import fpt.edu.vn.gms.dto.response.StockReceiptItemResponse;
import fpt.edu.vn.gms.dto.response.StockReceiptListResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

public interface StockReceiptService {

    Page<StockReceiptListResponse> getReceipts(String status, String keyword, String fromDate, String toDate,
            Long supplierId, Pageable pageable);

    StockReceiptDetailResponse getReceiptDetail(Long id);

    Page<StockReceiptItemResponse> getReceiptItems(Long receiptId, Pageable pageable);

    StockReceiptItemDetailResponse getReceiptItemDetail(Long itemId);

    StockReceiptItemDetailResponse getReceiptItemHistory(Long itemId);

    StockReceiptItemDetailResponse createReceiptItemHistory(Long itemId, CreateReceiptItemHistoryRequest request,
            MultipartFile file);

    StockReceiptDetailResponse cancelReceipt(Long id);

    StockReceiptDetailResponse completeReceipt(Long id);

    StockReceiptDetailResponse createReceiptFromPurchaseRequest(Long purchaseRequestId);

    StockReceiptItemHistoryDetailResponse getReceiptItemHistoryDetail(Long historyId);
}
