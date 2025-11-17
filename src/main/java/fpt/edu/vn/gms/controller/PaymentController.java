package fpt.edu.vn.gms.controller;

import fpt.edu.vn.gms.dto.PaymentVoucherDto;
import fpt.edu.vn.gms.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/voucher")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentVoucherService;

    // 1. Tìm voucher theo khách hàng, xe, hoặc quotation
    @GetMapping("/search")
    public List<PaymentVoucherDto> searchVouchers(
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String vehicle) {
        return paymentVoucherService.searchVouchers(phone, vehicle);
    }

    // 2. Lấy chi tiết voucher
    @GetMapping("/{id}")
    public PaymentVoucherDto getVoucher(@PathVariable Long id) {
        return paymentVoucherService.getVoucherById(id);
    }

    // 3. Thanh toán voucher
    @PostMapping("/{id}/pay")
    public PaymentVoucherDto payVoucher(
            @PathVariable Long id,
            @RequestParam String paymentMethod,
            @RequestParam BigDecimal amountPaid) {
        return paymentVoucherService.payVoucher(id, paymentMethod, amountPaid);
    }

    // 4. Lấy tất cả voucher của một quotation
    @GetMapping("/quotation/{quotationId}")
    public List<PaymentVoucherDto> getVouchersByQuotation(@PathVariable Long quotationId) {
        return paymentVoucherService.getVouchersByQuotation(quotationId);
    }

    // 5. Lấy link QR Sepay để thanh toán
    @GetMapping("/{id}/qr")
    public Map<String, String> getVoucherQr(@PathVariable Long id) {
        String qrUrl = paymentVoucherService.generateQrUrl(id);
        return Map.of("qrUrl", qrUrl);
    }
}
