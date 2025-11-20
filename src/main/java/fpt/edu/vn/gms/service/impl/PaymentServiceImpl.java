package fpt.edu.vn.gms.service.impl;

import fpt.edu.vn.gms.dto.PaymentVoucherDto;
import fpt.edu.vn.gms.entity.*;
import fpt.edu.vn.gms.exception.ResourceNotFoundException;
import fpt.edu.vn.gms.repository.PaymentVoucherRepository;
import fpt.edu.vn.gms.repository.PriceQuotationRepository;
import fpt.edu.vn.gms.repository.ServiceTicketRepository;
import fpt.edu.vn.gms.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentVoucherRepository voucherRepository;
    private final PriceQuotationRepository quotationRepository;
    private final ServiceTicketRepository serviceTicketRepository;

    @Value("${app.payment.deposit-rate}")
    private BigDecimal depositRate;

    @Override
    @Transactional
    public PaymentVoucher createDepositAndFinalVoucherIfNeeded(Long quotationId, Long serviceTicketId,
            String createdBy) {
        PriceQuotation quotation = quotationRepository.findById(quotationId)
                .orElseThrow(() -> new ResourceNotFoundException("Quotation not found: " + quotationId));

        ServiceTicket serviceTicket = serviceTicketRepository.findById(serviceTicketId)
                .orElseThrow(() -> new ResourceNotFoundException("Service Ticket not found: " + serviceTicketId));

        // compute totals
        BigDecimal totalQuotation = Optional.ofNullable(quotation.getEstimateAmount()).orElse(BigDecimal.ZERO);
        BigDecimal specialPartsTotal = BigDecimal.ZERO;
        boolean hasSpecial = false;

        for (PriceQuotationItem item : quotation.getItems()) {
            if (item.getItemType() != null && item.getItemType().name().equals("PART") && item.getPart() != null
                    && item.getPart().isSpecialPart()) {
                BigDecimal lineTotal = Optional.ofNullable(item.getTotalPrice()).orElse(BigDecimal.ZERO);
                specialPartsTotal = specialPartsTotal.add(lineTotal);
                hasSpecial = true;
            }
        }

        // create deposit if needed
        PaymentVoucher depositVoucher = null;
        if (hasSpecial && specialPartsTotal.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal depositAmount = specialPartsTotal.multiply(depositRate).setScale(2, BigDecimal.ROUND_HALF_UP);

            // if already exists, skip creating duplicate
            Optional<PaymentVoucher> existingDeposit = voucherRepository.findByQuotationIdAndType(quotationId,
                    PaymentVoucher.VoucherType.DEPOSIT);
            if (existingDeposit.isPresent()) {
                depositVoucher = existingDeposit.get();
                // if still DRAFT or PENDING, update amount if changed
                if (depositVoucher.getStatus() != PaymentVoucher.VoucherStatus.PAID) {
                    depositVoucher.setTotalAmount(depositAmount);
                    depositVoucher.setUpdatedAt(LocalDateTime.now());
                    voucherRepository.save(depositVoucher);
                }
            } else {
                PaymentVoucher v = PaymentVoucher.builder()
                        .quotationId(quotation)
                        .serviceTicketId(serviceTicket)
                        .type(PaymentVoucher.VoucherType.DEPOSIT)
                        .status(PaymentVoucher.VoucherStatus.PENDING)
                        .totalAmount(depositAmount)
                        .amountPaid(BigDecimal.ZERO)
                        .currency("VND")
                        .createdBy(createdBy)
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build();
                depositVoucher = voucherRepository.save(v);
            }
        }

        // create final voucher (remaining amount)
        Optional<PaymentVoucher> existingFinal = voucherRepository.findByQuotationIdAndType(quotationId,
                PaymentVoucher.VoucherType.FINAL);
        BigDecimal finalTotal = totalQuotation;
        if (depositVoucher != null)
            finalTotal = finalTotal.subtract(depositVoucher.getTotalAmount());
        if (finalTotal.compareTo(BigDecimal.ZERO) < 0)
            finalTotal = BigDecimal.ZERO;

        PaymentVoucher finalVoucher;
        if (existingFinal.isPresent()) {
            finalVoucher = existingFinal.get();
            if (finalVoucher.getStatus() != PaymentVoucher.VoucherStatus.PAID) {
                finalVoucher.setTotalAmount(finalTotal);
                finalVoucher.setUpdatedAt(LocalDateTime.now());
                finalVoucher = voucherRepository.save(finalVoucher);
            }
        } else {
            finalVoucher = PaymentVoucher.builder()
                    .quotationId(quotation)
                    .serviceTicketId(serviceTicket)
                    .type(PaymentVoucher.VoucherType.FINAL)
                    .status(PaymentVoucher.VoucherStatus.DRAFT) // normally set to PENDING when waiting handover
                    .totalAmount(finalTotal)
                    .amountPaid(BigDecimal.ZERO)
                    .currency("VND")
                    .createdBy(createdBy)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();
            finalVoucher = voucherRepository.save(finalVoucher);
        }

        return depositVoucher != null ? depositVoucher : finalVoucher;
    }

    @Override
    public Transaction handleSepayWebhook(String providerTxnId, String payload) {
        return null;
    }

    @Override
    public PaymentVoucher getVoucher(Long id) {
        return voucherRepository.findById(id).orElseThrow(() -> new RuntimeException("Voucher not found"));
    }

    @Override
    public List<PaymentVoucherDto> searchVouchers(String phone, String vehicle) {
        // TODO: implement tìm kiếm với các điều kiện không bắt buộc
        return voucherRepository.search(phone, vehicle)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public PaymentVoucherDto getVoucherById(Long id) {
        PaymentVoucher voucher = voucherRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Voucher not found"));
        return toDto(voucher);
    }

    @Override
    public PaymentVoucherDto payVoucher(Long id, String paymentMethod, BigDecimal amountPaid) {
        PaymentVoucher voucher = voucherRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Voucher not found"));

        // TODO: logic thanh toán
        voucher.setAmountPaid(voucher.getAmountPaid().add(amountPaid));
        if (voucher.getAmountPaid().compareTo(voucher.getAmountPaid()) >= 0) {
            voucher.setStatus(PaymentVoucher.VoucherStatus.PAID);
        } else {
            voucher.setStatus(PaymentVoucher.VoucherStatus.PARTIALLY_PAID);
        }
        voucherRepository.save(voucher);
        return toDto(voucher);
    }

    @Override
    public List<PaymentVoucherDto> getVouchersByQuotation(Long quotationId) {
        return voucherRepository.findByQuotationId_PriceQuotationId(quotationId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public String generateQrUrl(Long id) {
        // TODO: gọi Sepay API tạo link QR
        return "https://sepay.example.com/pay?voucherId=" + id;
    }

    private PaymentVoucherDto toDto(PaymentVoucher voucher) {
        return new PaymentVoucherDto(
                voucher.getId(),
                voucher.getServiceTicketId().getCustomerName(),
                voucher.getServiceTicketId().getVehicle().getLicensePlate(),
                voucher.getQuotationId().getCode(),
                voucher.getTotalAmount(),
                voucher.getAmountPaid(),
                voucher.getType().toString(),
                voucher.getStatus().toString());
    }
}
