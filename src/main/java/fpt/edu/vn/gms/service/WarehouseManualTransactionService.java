package fpt.edu.vn.gms.service;

import fpt.edu.vn.gms.dto.request.ManualTransactionRequest;
import fpt.edu.vn.gms.dto.response.ManualTransactionResponse;

public interface WarehouseManualTransactionService {

    /**
     * Tạo phiếu xuất kho / nhập kho thủ công.
     * - type = EXPORT: tạo StockExport + StockExportItem
     * - type = RECEIPT: tạo StockReceipt + StockReceiptItem
     */
    ManualTransactionResponse createManualTransaction(ManualTransactionRequest request);
}

