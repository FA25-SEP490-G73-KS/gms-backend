package fpt.edu.vn.gms.service.impl;

import fpt.edu.vn.gms.common.PurchaseOrderStatus;
import fpt.edu.vn.gms.dto.response.PurchaseOrderResponseDto;
import fpt.edu.vn.gms.entity.CodeSequence;
import fpt.edu.vn.gms.entity.PurchaseOrder;
import fpt.edu.vn.gms.entity.PurchaseRequest;
import fpt.edu.vn.gms.exception.ResourceNotFoundException;
import fpt.edu.vn.gms.mapper.PurchaseOrderMapper;
import fpt.edu.vn.gms.repository.PurchaseOrderRepository;
import fpt.edu.vn.gms.repository.PurchaseRequestRepository;
import fpt.edu.vn.gms.service.CodeSequenceService;
import fpt.edu.vn.gms.service.PurchaseOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PurchaseOrderServiceImpl implements PurchaseOrderService {

    private final PurchaseRequestRepository purchaseRequestRepo;
    private final PurchaseOrderRepository purchaseOrderRepo;
    private final PurchaseOrderMapper purchaseOrderMapper;
    private final CodeSequenceService codeSequenceService;

    @Transactional
    public PurchaseOrderResponseDto createFromPurchaseRequest(Long requestId) {
        PurchaseRequest pr = purchaseRequestRepo.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy yêu cầu mua hàng"));

        PurchaseOrder po = PurchaseOrder.builder()
                .code(codeSequenceService.generateCode("PO"))
                .purchaseRequest(pr)
                .status(PurchaseOrderStatus.CREATED)
                .createdAt(LocalDateTime.now())
                .totalAmount(BigDecimal.ZERO)
                .build();

        purchaseOrderRepo.save(po);

        return purchaseOrderMapper.toDto(po);
    }

    @Override
    public PurchaseOrderResponseDto getById(Long id) {

        PurchaseOrder po = purchaseOrderRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy đơn đặt hàng"));
        return purchaseOrderMapper.toDto(po);
    }

    @Override
    public Page<PurchaseOrderResponseDto> getAll(int page, int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<PurchaseOrder> poPage = purchaseOrderRepo.findAll(pageable);

        return poPage.map(purchaseOrderMapper::toDto);
    }
}
