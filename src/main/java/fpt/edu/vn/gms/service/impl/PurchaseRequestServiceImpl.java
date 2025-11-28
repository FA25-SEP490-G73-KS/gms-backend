package fpt.edu.vn.gms.service.impl;

import fpt.edu.vn.gms.common.enums.*;
import fpt.edu.vn.gms.common.enums.Role;
import fpt.edu.vn.gms.dto.response.PurchaseRequestDetailDto;
import fpt.edu.vn.gms.dto.response.PurchaseRequestItemResponseDto;
import fpt.edu.vn.gms.dto.response.PurchaseRequestResponseDto;
import fpt.edu.vn.gms.entity.*;
import fpt.edu.vn.gms.exception.ResourceNotFoundException;
import fpt.edu.vn.gms.mapper.PurchaseRequestDetailMapper;
import fpt.edu.vn.gms.mapper.PurchaseRequestItemMapper;
import fpt.edu.vn.gms.mapper.PurchaseRequestMapper;
import fpt.edu.vn.gms.repository.*;
import fpt.edu.vn.gms.service.NotificationService;
import fpt.edu.vn.gms.service.PurchaseRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
public class PurchaseRequestServiceImpl implements PurchaseRequestService {

    private final PurchaseRequestRepository purchaseRequestRepo;
    private final PurchaseRequestItemRepository purchaseRequestItemRepo;
    private final AccountRepository accountRepository;
    private final NotificationService notificationService;
    private final PurchaseRequestMapper purchaseRequestMapper;
    private final PurchaseRequestItemMapper purchaseRequestItemMapper;
    private final PurchaseRequestDetailMapper purchaseRequestDetailMapper;

    public Page<PurchaseRequestResponseDto> getPurchaseRequests(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<PurchaseRequest> prPage = purchaseRequestRepo.findAll(pageable);
        return prPage.map(purchaseRequestMapper::toResponseDto);
    }

    public PurchaseRequestDetailDto getPurchaseRequestItems(Long prId) {
        PurchaseRequest item = purchaseRequestRepo.findById(prId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy pr + " + prId));
        return purchaseRequestDetailMapper.toDetailDto(item);
    }

    @Transactional
    @Override
    public PurchaseRequestItemResponseDto reviewItem(Long itemId, boolean approve, String note, Employee currentUser) {

        PurchaseRequestItem item = purchaseRequestItemRepo.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy PurchaseRequestItem ID: " + itemId));

        item.setReviewStatus(approve
                ? ManagerReviewStatus.APPROVED
                : ManagerReviewStatus.REJECTED);

        item.setNote(note);
        item.setUpdatedAt(LocalDateTime.now());
        item.setUpdatedBy(currentUser.getEmployeeId());

        purchaseRequestItemRepo.save(item);

        updatePRStatus(item.getPurchaseRequest());

        return purchaseRequestItemMapper.toResponseDto(item);
    }

    // ---------------------------
    // CẬP NHẬT TRẠNG THÁI PR SAU KHI REVIEW ITEM
    // ---------------------------
    @Transactional
    protected void updatePRStatus(PurchaseRequest pr) {

        List<PurchaseRequestItem> items = pr.getItems();

        boolean allApproved = items.stream()
                .allMatch(i -> i.getReviewStatus() == ManagerReviewStatus.APPROVED);

        boolean anyRejected = items.stream()
                .anyMatch(i -> i.getReviewStatus() == ManagerReviewStatus.REJECTED);

        if (anyRejected) {
            pr.setReviewStatus(ManagerReviewStatus.REJECTED);
            purchaseRequestRepo.save(pr);
            notifyWarehouse(pr, NotificationTemplate.PURCHASE_REQUEST_REJECTED);
            return;
        }

        if (allApproved) {
            pr.setReviewStatus(ManagerReviewStatus.APPROVED);
            purchaseRequestRepo.save(pr);
            notifyWarehouse(pr, NotificationTemplate.PURCHASE_REQUEST_CONFIRMED);
            return;
        }

        pr.setReviewStatus(ManagerReviewStatus.PENDING);
        purchaseRequestRepo.save(pr);
    }

    // ---------------------------
    // GỬI THÔNG BÁO CHO WAREHOUSE
    // ---------------------------
    private void notifyWarehouse(PurchaseRequest pr, NotificationTemplate template) {

        List<Account> warehouseAccounts = accountRepository.findByRole(Role.WAREHOUSE);

        warehouseAccounts.forEach(acc -> {
            if (acc.getEmployee() == null) return;

            notificationService.createNotification(
                    acc.getEmployee().getEmployeeId(),
                    template.getTitle(),
                    template.format(pr.getId()),
                    NotificationType.PURCHASE_REQUEST,
                    pr.getId().toString(),
                    "/purchase-requests/" + pr.getId()
            );
        });
    }


}
