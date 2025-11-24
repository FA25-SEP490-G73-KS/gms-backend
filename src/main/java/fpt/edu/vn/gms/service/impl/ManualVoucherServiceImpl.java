package fpt.edu.vn.gms.service.impl;

import fpt.edu.vn.gms.common.enums.ManualVoucherStatus;
import fpt.edu.vn.gms.common.enums.ManualVoucherType;
import fpt.edu.vn.gms.dto.request.ExpenseVoucherCreateRequest;
import fpt.edu.vn.gms.dto.request.ManualVoucherCreateRequest;
import fpt.edu.vn.gms.dto.response.ManualVoucherResponseDto;
import fpt.edu.vn.gms.entity.Employee;
import fpt.edu.vn.gms.entity.ManualVoucher;
import fpt.edu.vn.gms.entity.PurchaseRequestItem;
import fpt.edu.vn.gms.entity.StockReceiptItem;
import fpt.edu.vn.gms.exception.ResourceNotFoundException;
import fpt.edu.vn.gms.mapper.ManualVoucherMapper;
import fpt.edu.vn.gms.repository.EmployeeRepository;
import fpt.edu.vn.gms.repository.ManualVoucherRepository;
import fpt.edu.vn.gms.repository.StockReceiptItemRepository;
import fpt.edu.vn.gms.service.CodeSequenceService;
import fpt.edu.vn.gms.service.ManualVoucherService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ManualVoucherServiceImpl implements ManualVoucherService {

    ManualVoucherRepository manualRepo;
    StockReceiptItemRepository stockReceiptItemRepo;
    EmployeeRepository employeeRepo;
    ManualVoucherMapper manualVoucherMapper;
    CodeSequenceService codeSequenceService;
    private final FileStorageService fileStorageService;

    @Transactional
    @Override
    public ManualVoucherResponseDto payForStockReceiptItem(Long itemId,
                                                           ExpenseVoucherCreateRequest request,
                                                           Employee accountant) {

        log.info("[ACCOUNTING][PAY] itemId={} req={}", itemId, request);

        StockReceiptItem item = stockReceiptItemRepo.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy dòng nhập kho"));

        if (manualRepo.existsByStockReceiptItem(item)) {
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
        ManualVoucher voucher = ManualVoucher.builder()
                .code(codeSequenceService.generateCode("PC"))
                .type(ManualVoucherType.CHI)
                .status(ManualVoucherStatus.APPROVED)
                .amount(amount)
                .target("NCC")
                .description(request.getDescription())
                .attachmentUrl(request.getAttachmentUrl())
                .createdBy(accountant)
                .createdAt(LocalDateTime.now())
                .stockReceiptItem(item)
                .build();

        manualRepo.save(voucher);

        // ===== Mark paid =====
        item.setPaid(true);
        stockReceiptItemRepo.save(item);

        log.info("[ACCOUNTING][PAY] DONE: itemId={} amount={}", itemId, amount);

        return manualVoucherMapper.toDto(voucher);
    }

    @Override
    @Transactional
    public ManualVoucherResponseDto create(ManualVoucherCreateRequest req,
                                           MultipartFile file,
                                           Employee creator) {

        String fileUrl = null;

        if (file != null && !file.isEmpty()) {
            fileUrl = fileStorageService.upload(file);
        }

        String prefix = req.getType() == ManualVoucherType.CHI ? "CHI" : "THU";

        Employee employee = employeeRepo.findById(req.getApprovedByEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy quản lý!"));

        ManualVoucher voucher = ManualVoucher.builder()
                .code(codeSequenceService.generateCode(prefix))
                .type(req.getType())
                .amount(req.getAmount())
                .target(req.getTarget())
                .description(req.getDescription())
                .attachmentUrl(fileUrl)
                .createdBy(creator)
                .status(ManualVoucherStatus.PENDING)
                .approvedBy(employee)
                .build();

        Employee approver = employeeRepo.findById(req.getApprovedByEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người duyệt"));

        voucher.setApprovedBy(approver);

        manualRepo.save(voucher);

        return manualVoucherMapper.toDto(voucher);
    }

}
