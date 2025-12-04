package fpt.edu.vn.gms.service.impl;

import fpt.edu.vn.gms.common.enums.LedgerVoucherStatus;
import fpt.edu.vn.gms.common.enums.LedgerVoucherType;
import fpt.edu.vn.gms.common.enums.ReceiptPaymentStatus;
import fpt.edu.vn.gms.dto.request.ApproveVoucherRequest;
import fpt.edu.vn.gms.dto.request.CreateVoucherRequest;
import fpt.edu.vn.gms.dto.request.UpdateVoucherRequest;
import fpt.edu.vn.gms.dto.response.LedgerVoucherDetailResponse;
import fpt.edu.vn.gms.dto.response.LedgerVoucherListResponse;
import fpt.edu.vn.gms.entity.Employee;
import fpt.edu.vn.gms.entity.LedgerVoucher;
import fpt.edu.vn.gms.entity.StockReceiptItemHistory;
import fpt.edu.vn.gms.exception.ResourceNotFoundException;
import fpt.edu.vn.gms.mapper.LedgerVoucherMapper;
import fpt.edu.vn.gms.repository.EmployeeRepository;
import fpt.edu.vn.gms.repository.LedgerVoucherRepository;
import fpt.edu.vn.gms.repository.StockReceiptItemHistoryRepository;
import fpt.edu.vn.gms.service.CodeSequenceService;
import fpt.edu.vn.gms.service.LedgerVoucherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
public class LedgerVoucherServiceImpl implements LedgerVoucherService {

    private final LedgerVoucherRepository ledgerVoucherRepository;
    private final StockReceiptItemHistoryRepository stockReceiptItemHistoryRepository;
    private final EmployeeRepository employeeRepository;
    private final LedgerVoucherMapper mapper;
    private final CodeSequenceService codeSequenceService;
    private final FileStorageService fileStorageService;

    @Override
    @Transactional
    public LedgerVoucherDetailResponse createManualVoucher(CreateVoucherRequest request) {
        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Số tiền phải > 0");
        }

        LedgerVoucher voucher = mapper.toEntity(request);
        voucher.setCode(generateVoucherCode(request.getType()));
        voucher.setStatus(LedgerVoucherStatus.PENDING);

        LedgerVoucher saved = ledgerVoucherRepository.save(voucher);
        return mapper.toDetailDto(saved);
    }

    @Override
    @Transactional
    public LedgerVoucherDetailResponse createPaymentVoucherFromReceiptHistory(Long receiptHistoryId, CreateVoucherRequest request, MultipartFile file) {
        StockReceiptItemHistory history = stockReceiptItemHistoryRepository.findById(receiptHistoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy lịch sử nhập kho"));

        if (history.getPaymentStatus() == ReceiptPaymentStatus.PAID) {
            throw new IllegalStateException("Lịch sử nhập kho này đã được thanh toán");
        }

        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Số tiền phải > 0");
        }

        String attachmentUrl = null;
        if (file != null && !file.isEmpty()) {
            attachmentUrl = fileStorageService.upload(file);
        }

        LedgerVoucher voucher = LedgerVoucher.builder()
                .code(generateVoucherCode(LedgerVoucherType.STOCK_RECEIPT_PAYMENT))
                .type(LedgerVoucherType.STOCK_RECEIPT_PAYMENT)
                .amount(request.getAmount())
                .relatedSupplierId(request.getRelatedSupplierId())
                .description(request.getDescription())
                .attachmentUrl(attachmentUrl)
                .status(LedgerVoucherStatus.PENDING)
                .receiptHistory(history)
                .build();

        LedgerVoucher saved = ledgerVoucherRepository.save(voucher);

        history.setPaymentStatus(ReceiptPaymentStatus.PAID);
        history.setAmountPaid(request.getAmount());
        stockReceiptItemHistoryRepository.save(history);

        return mapper.toDetailDto(saved);
    }

    @Override
    @Transactional
    public LedgerVoucherDetailResponse updateVoucher(Long id, UpdateVoucherRequest request) {
        LedgerVoucher voucher = ledgerVoucherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phiếu thu/chi"));

        if (voucher.getStatus() != LedgerVoucherStatus.PENDING) {
            throw new IllegalStateException("Chỉ được cập nhật phiếu ở trạng thái Chờ duyệt");
        }

        mapper.updateEntityFromRequest(request, voucher);
        LedgerVoucher saved = ledgerVoucherRepository.save(voucher);
        return mapper.toDetailDto(saved);
    }

    @Override
    @Transactional
    public LedgerVoucherDetailResponse approveVoucher(Long id, ApproveVoucherRequest request) {
        LedgerVoucher voucher = ledgerVoucherRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phiếu thu/chi"));

        if (voucher.getStatus() != LedgerVoucherStatus.PENDING) {
            throw new IllegalStateException("Chỉ được duyệt phiếu ở trạng thái Chờ duyệt");
        }

        Employee approver = employeeRepository.findById(request.getApprovedByEmployeeId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nhân viên duyệt"));

        voucher.setApprovedBy(approver);
        voucher.setApprovedAt(LocalDateTime.now());
        voucher.setStatus(LedgerVoucherStatus.APPROVED);

        LedgerVoucher saved = ledgerVoucherRepository.save(voucher);
        return mapper.toDetailDto(saved);
    }

    @Override
    @Transactional
    public LedgerVoucherDetailResponse rejectVoucher(Long id) {
        LedgerVoucher voucher = ledgerVoucherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phiếu thu/chi"));

        if (voucher.getStatus() != LedgerVoucherStatus.PENDING) {
            throw new IllegalStateException("Chỉ được từ chối phiếu ở trạng thái Chờ duyệt");
        }

        voucher.setStatus(LedgerVoucherStatus.REJECTED);
        LedgerVoucher saved = ledgerVoucherRepository.save(voucher);
        return mapper.toDetailDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public LedgerVoucherDetailResponse getVoucherDetail(Long id) {
        LedgerVoucher voucher = ledgerVoucherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phiếu thu/chi"));
        return mapper.toDetailDto(voucher);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<LedgerVoucherListResponse> getVoucherList(String keyword,
                                                          String type,
                                                          String status,
                                                          String fromDate,
                                                          String toDate,
                                                          Long supplierId,
                                                          Long employeeId,
                                                          Pageable pageable) {
        Specification<LedgerVoucher> spec = Specification.unrestricted();

        if (keyword != null && !keyword.isBlank()) {
            String kw = keyword.trim().toLowerCase();
            spec = spec.and((root, query, cb) ->
                    cb.like(cb.lower(root.get("code")), "%" + kw + "%"));
        }

        if (type != null && !type.isBlank()) {
            LedgerVoucherType t = LedgerVoucherType.valueOf(type);
            spec = spec.and((root, query, cb) -> cb.equal(root.get("type"), t));
        }

        if (status != null && !status.isBlank()) {
            LedgerVoucherStatus st = LedgerVoucherStatus.valueOf(status);
            spec = spec.and((root, query, cb) -> cb.equal(root.get("status"), st));
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        if (fromDate != null && !fromDate.isBlank()) {
            LocalDate from = LocalDate.parse(fromDate, formatter);
            LocalDateTime fromDateTime = from.atStartOfDay();
            spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("createdAt"), fromDateTime));
        }
        if (toDate != null && !toDate.isBlank()) {
            LocalDate to = LocalDate.parse(toDate, formatter);
            LocalDateTime toDateTime = to.plusDays(1).atStartOfDay();
            spec = spec.and((root, query, cb) -> cb.lessThan(root.get("createdAt"), toDateTime));
        }

        if (supplierId != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("relatedSupplierId"), supplierId));
        }

        if (employeeId != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("relatedEmployeeId"), employeeId));
        }

        Page<LedgerVoucher> page = ledgerVoucherRepository.findAll(spec, pageable);
        return page.map(mapper::toListDto);
    }

    private String generateVoucherCode(LedgerVoucherType type) {
        String prefix;
        if (type == LedgerVoucherType.SALARY || type == LedgerVoucherType.SERVICE_FEE || type == LedgerVoucherType.STOCK_RECEIPT_PAYMENT || type == LedgerVoucherType.OTHER) {
            prefix = "PC"; // Phiếu chi
        } else {
            prefix = "PT"; // Phiếu thu (nếu sau này có thêm loại thu)
        }
        return codeSequenceService.generateCode(prefix);
    }

    @Override
    @Transactional
    public LedgerVoucherDetailResponse payVoucher(Long id) {
        LedgerVoucher voucher = ledgerVoucherRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phiếu thu/chi"));

        if (voucher.getStatus() != LedgerVoucherStatus.APPROVED) {
            throw new IllegalStateException("Chỉ được chuyển sang Hoàn tất khi phiếu đang ở trạng thái Đã duyệt");
        }

        voucher.setStatus(LedgerVoucherStatus.FINISHED);
        LedgerVoucher saved = ledgerVoucherRepository.save(voucher);
        return mapper.toDetailDto(saved);
    }
}
