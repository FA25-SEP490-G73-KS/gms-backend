package fpt.edu.vn.gms.service.impl;

import fpt.edu.vn.gms.common.enums.LedgerVoucherCategory;
import fpt.edu.vn.gms.common.enums.ManualVoucherStatus;
import fpt.edu.vn.gms.common.enums.ManualVoucherType;
import fpt.edu.vn.gms.dto.request.ExpenseVoucherCreateRequest;
import fpt.edu.vn.gms.dto.request.ManualVoucherCreateRequest;
import fpt.edu.vn.gms.dto.response.ManualVoucherListResponseDto;
import fpt.edu.vn.gms.dto.response.ManualVoucherResponseDto;
import fpt.edu.vn.gms.entity.*;
import fpt.edu.vn.gms.exception.ResourceNotFoundException;
import fpt.edu.vn.gms.mapper.ManualVoucherMapper;
import fpt.edu.vn.gms.repository.EmployeeRepository;
import fpt.edu.vn.gms.repository.ManualVoucherRepository;
import fpt.edu.vn.gms.repository.StockReceiptItemRepository;
import fpt.edu.vn.gms.repository.SupplierRepository;
import fpt.edu.vn.gms.service.CodeSequenceService;
import fpt.edu.vn.gms.service.ManualVoucherService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    SupplierRepository supplierRepo;
    ManualVoucherMapper manualVoucherMapper;
    CodeSequenceService codeSequenceService;
    FileStorageService fileStorageService;


    @Transactional
    @Override
    public ManualVoucherResponseDto payForStockReceiptItem(Long itemId,
                                                           ExpenseVoucherCreateRequest request,
                                                           Employee accountant) {

        log.info("[ACCOUNTING][PAY] itemId={} req={}", itemId, request);

        StockReceiptItem item = stockReceiptItemRepo.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy dòng nhập kho"));

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
        LedgerVoucher voucher = LedgerVoucher.builder()
                .code(codeSequenceService.generateCode("PAY"))
                .type(ManualVoucherType.PAYMENT)
                .category(LedgerVoucherCategory.SUPPLIER_PAYMENT)
                .status(ManualVoucherStatus.FINISHED)
                .amount(amount)
                .relatedSupplierId(request.getSupplierId())
                .attachmentUrl(request.getAttachmentUrl())
                .createdBy(accountant)
                .createdAt(LocalDateTime.now())
                .build();

        manualRepo.save(voucher);

        // ===== Mark paid =====
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

        String prefix = req.getType() == ManualVoucherType.PAYMENT ? "PAY" : "RECEIPT";

        Employee employee = employeeRepo.findById(3L)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy quản lý!"));


        LedgerVoucher voucher = LedgerVoucher.builder()
                .code(codeSequenceService.generateCode(prefix))
                .type(req.getType())
                .category(req.getCategory())
                .amount(req.getAmount())
                .description(req.getDescription())
                .relatedSupplierId(req.getRelatedSupplierId())
                .relatedEmployeeId(req.getRelatedEmployeeId())
                .attachmentUrl(fileUrl)
                .createdBy(creator)
                .status(ManualVoucherStatus.PENDING)
                .approvedBy(employee)
                .createdAt(LocalDateTime.now())
                .build();


        manualRepo.save(voucher);

        ManualVoucherResponseDto dto = manualVoucherMapper.toDto(voucher);
        dto.setTargetName(resolveTargetName(voucher));

        return dto;
    }

    @Override
    public Page<ManualVoucherListResponseDto> getList(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Page<LedgerVoucher> manualVouchers = manualRepo.findAll(pageable);

        return manualVouchers.map(mv -> {
            ManualVoucherListResponseDto dto = manualVoucherMapper.toListDto(mv);

            // set targetName ở đây
            dto.setTargetName(resolveTargetName(mv));

            return dto;
        });
    }

    @Override
    public ManualVoucherResponseDto getDetail(Long id) {
        LedgerVoucher voucher = manualRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phiếu thu/chi"));

        ManualVoucherResponseDto dto = manualVoucherMapper.toDto(voucher);
        dto.setTargetName(resolveTargetName(voucher));

        return dto;
    }

    private String resolveTargetName(LedgerVoucher mv) {

        // 1. Người nhận (chi lương)
        if (mv.getRelatedEmployeeId() != null) {
            return employeeRepo.findById(mv.getRelatedEmployeeId())
                    .map(Employee::getFullName)
                    .orElse("Nhân viên không tồn tại");
        }

        // 2. Nhà cung cấp
        if (mv.getRelatedSupplierId() != null) {
            return supplierRepo.findById(mv.getRelatedSupplierId())
                    .map(Supplier::getName)
                    .orElse("NCC không tồn tại");
        }

        // 3. Không xác định
        return "Không xác định";
    }

    @Override
    public ManualVoucherResponseDto approveVoucher(Long voucherId, Employee approver) {

        LedgerVoucher voucher = manualRepo.findById(voucherId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phiếu thu/chi"));

        if (voucher.getStatus() == ManualVoucherStatus.APPROVED) {
            throw new RuntimeException("Phiếu đã được duyệt trước đó");
        }

        voucher.setStatus(ManualVoucherStatus.APPROVED);
        voucher.setApprovedAt(LocalDateTime.now());
        voucher.setApprovedBy(approver);

        manualRepo.save(voucher);

        // Map sang DTO
        return ManualVoucherResponseDto.builder()
                .id(voucher.getId())
                .code(voucher.getCode())
                .type(voucher.getType().getValue())
                .amount(voucher.getAmount())
                .description(voucher.getDescription())
                .status(voucher.getStatus().getValue())
                .approvedAt(voucher.getApprovedAt())
                .approvedBy(voucher.getApprovedBy().getFullName())
                .attachmentUrl(voucher.getAttachmentUrl())
                .build();
    }

    @Override
    public ManualVoucherResponseDto finishVoucher(Long voucherId) {
        LedgerVoucher voucher = manualRepo.findById(voucherId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phiếu thu/chi"));
        if (voucher.getStatus() != ManualVoucherStatus.APPROVED) {
            throw new RuntimeException("Chỉ được chi tiền cho phiếu đã duyệt");
        }
        voucher.setStatus(ManualVoucherStatus.FINISHED);
        voucher.setApprovedAt(java.time.LocalDateTime.now());
        manualRepo.save(voucher);
        return manualVoucherMapper.toDto(voucher);
    }
}
