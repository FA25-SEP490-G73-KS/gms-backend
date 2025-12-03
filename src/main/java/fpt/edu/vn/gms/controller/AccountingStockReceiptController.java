package fpt.edu.vn.gms.controller;


import fpt.edu.vn.gms.service.ManualVoucherService;
import fpt.edu.vn.gms.service.StockReceiptService;
import fpt.edu.vn.gms.utils.AppRoutes;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(path = AppRoutes.ACCOUNTING_STOCK_RECEIPT_PREFIX,
        produces = MediaType.APPLICATION_JSON_VALUE)
@CrossOrigin(origins = "${fe-local-host}")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "accounting-stock-receipts", description = "Kế toán xử lý tiền vật tư")
public class AccountingStockReceiptController {

    private final StockReceiptService stockReceiptService;
    private final ManualVoucherService manualVoucherService;

}
