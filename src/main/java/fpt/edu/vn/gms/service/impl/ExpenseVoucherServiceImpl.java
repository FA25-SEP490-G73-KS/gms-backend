package fpt.edu.vn.gms.service.impl;

import fpt.edu.vn.gms.common.enums.ExpenseVoucherStatus;
import fpt.edu.vn.gms.common.enums.ExpenseVoucherType;
import fpt.edu.vn.gms.dto.request.ExpenseVoucherCreateRequest;
import fpt.edu.vn.gms.dto.response.ExpenseVoucherResponseDto;
import fpt.edu.vn.gms.entity.Employee;
import fpt.edu.vn.gms.entity.ExpenseVoucher;
import fpt.edu.vn.gms.entity.PurchaseRequestItem;
import fpt.edu.vn.gms.entity.StockReceiptItem;
import fpt.edu.vn.gms.exception.ResourceNotFoundException;
import fpt.edu.vn.gms.mapper.ExpenseVoucherMapper;
import fpt.edu.vn.gms.repository.ExpenseVoucherRepository;
import fpt.edu.vn.gms.repository.StockReceiptItemRepository;
import fpt.edu.vn.gms.service.CodeSequenceService;
import fpt.edu.vn.gms.service.ExpenseVoucherService;
import fpt.edu.vn.gms.service.NotificationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ExpenseVoucherServiceImpl implements ExpenseVoucherService {

    ExpenseVoucherRepository expenseVoucherRepo;
    StockReceiptItemRepository stockReceiptItemRepo;
    ExpenseVoucherMapper expenseVoucherMapper;
    CodeSequenceService codeSequenceService;

    @Transactional
    @Override
    public ExpenseVoucherResponseDto payForStockReceiptItem(Long itemId,
                                                            ExpenseVoucherCreateRequest request,
                                                            Employee accountant) {

        log.info("[ACCOUNTING][PAY] itemId={} req={}", itemId, request);

        StockReceiptItem item = stockReceiptItemRepo.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy dòng nhập kho"));

        if (expenseVoucherRepo.existsByStockReceiptItem(item)) {
            throw new IllegalStateException("Dòng nhập kho này đã được thanh toán");
        }

        PurchaseRequestItem prItem = item.getPurchaseRequestItem();

        MathContext mc = new MathContext(10, RoundingMode.HALF_UP);

        // ===== Tính đơn giá =====
        BigDecimal unitPrice = Optional.ofNullable(item.getActualUnitPrice())
                .orElseGet(() -> {
                    if (prItem.getEstimatedPurchasePrice() != null && prItem.getQuantity() != null) {
                        return prItem.getEstimatedPurchasePrice()
                                .divide(BigDecimal.valueOf(prItem.getQuantity()), mc);
                    }
                    throw new IllegalStateException("Không có đơn giá để tính số tiền chi");
                });

        BigDecimal amount = unitPrice.multiply(BigDecimal.valueOf(item.getQuantityReceived()));

        // ===== Tạo phiếu chi =====
        ExpenseVoucher voucher = ExpenseVoucher.builder()
                .code(codeSequenceService.generateCode("PC"))
                .type(ExpenseVoucherType.NCC)
                .status(ExpenseVoucherStatus.APPROVED)
                .amount(amount)
                .target("NCC")
                .description(request.getDescription())
                .attachmentUrl(request.getAttachmentUrl())
                .createdBy(accountant)
                .createdAt(LocalDateTime.now())
                .stockReceiptItem(item)
                .build();

        expenseVoucherRepo.save(voucher);

        // ===== Mark paid =====
        item.setPaid(true);
        stockReceiptItemRepo.save(item);

        log.info("[ACCOUNTING][PAY] DONE: itemId={} amount={}", itemId, amount);

        return expenseVoucherMapper.toDto(voucher);
    }
}
