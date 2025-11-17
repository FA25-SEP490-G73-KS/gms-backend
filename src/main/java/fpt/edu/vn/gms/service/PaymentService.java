package fpt.edu.vn.gms.service;

import fpt.edu.vn.gms.dto.PaymentVoucherDto;
import fpt.edu.vn.gms.entity.PaymentTransaction;
import fpt.edu.vn.gms.entity.PaymentVoucher;

import java.math.BigDecimal;
import java.util.List;

public interface PaymentService {

    PaymentVoucher createDepositAndFinalVoucherIfNeeded(Long quotationId, Long serviceTicketId, String createdBy);
    PaymentVoucher getVoucher(Long id);
    PaymentTransaction handleSepayWebhook(String providerTxnId, String payload);

    List<PaymentVoucherDto> searchVouchers(String phone, String vehicle);
    PaymentVoucherDto getVoucherById(Long id);
    PaymentVoucherDto payVoucher(Long id, String paymentMethod, BigDecimal amountPaid);
    List<PaymentVoucherDto> getVouchersByQuotation(Long quotationId);
    String generateQrUrl(Long id);
}
