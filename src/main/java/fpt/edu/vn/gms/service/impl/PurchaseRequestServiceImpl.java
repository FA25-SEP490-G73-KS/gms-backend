package fpt.edu.vn.gms.service.impl;

import fpt.edu.vn.gms.common.*;
import fpt.edu.vn.gms.common.enums.PurchaseReqItemStatus;
import fpt.edu.vn.gms.common.enums.PurchaseRequestStatus;
import fpt.edu.vn.gms.dto.response.PurchaseRequestItemResponseDto;
import fpt.edu.vn.gms.dto.response.PurchaseRequestResponseDto;
import fpt.edu.vn.gms.entity.*;
import fpt.edu.vn.gms.exception.ResourceNotFoundException;
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
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class PurchaseRequestServiceImpl implements PurchaseRequestService {

    private final PurchaseRequestRepository purchaseRequestRepo;
    private final PurchaseRequestItemRepository purchaseRequestItemRepo;
    private final AccountRepository accountRepository;
    private final NotificationService notificationService;
    private final PurchaseRequestMapper purchaseRequestMapper;
    private final PurchaseRequestItemMapper purchaseRequestItemMapper;

    public Page<PurchaseRequestResponseDto> getPurchaseRequests(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<PurchaseRequest> prPage = purchaseRequestRepo.findAllByOrderByCreatedAtDesc(pageable);
        return prPage.map(purchaseRequestMapper::toResponseDto);
    }

    public List<PurchaseRequestItemResponseDto> getPurchaseRequestItems(Long prId) {
        List<PurchaseRequestItem> items = purchaseRequestItemRepo.findByPurchaseRequestId(prId);
        return items.stream().map(purchaseRequestItemMapper::toResponseDto).collect(Collectors.toList());
    }

    @Transactional
    public PurchaseRequestItemResponseDto confirmPurchaseRequestItem(Long itemId, boolean approved, String note) {
        PurchaseRequestItem item = purchaseRequestItemRepo.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy PR item ID: " + itemId));

        item.setReviewStatus(approved ? ManagerReviewStatus.CONFIRMED : ManagerReviewStatus.REJECTED);
        item.setNote(note);
        item.setUpdated(LocalDateTime.now());
        purchaseRequestItemRepo.save(item);

        // Cập nhật tổng trạng thái PR
        updatePurchaseRequestStatus(item.getPurchaseRequest());

        return purchaseRequestItemMapper.toResponseDto(item);
    }

    private void updatePurchaseRequestStatus(PurchaseRequest pr) {
        boolean allApproved = pr.getItems().stream()
                .allMatch(i -> i.getReviewStatus() == ManagerReviewStatus.CONFIRMED);


        if (allApproved) {
            pr.setReviewStatus(ManagerReviewStatus.CONFIRMED);

            NotificationTemplate template = NotificationTemplate.PURCHASE_REQUEST_CONFIRMED;

            // Lấy tất cả account có role Warehouse
            List<Account> warehouseAccounts = accountRepository.findByRoleRoleName("Warehouse");

            // Gửi notification cho từng account
            warehouseAccounts.forEach(account -> {
                if (account.getEmployee() != null) {
                    notificationService.createNotification(
                            account.getEmployee().getEmployeeId(),
                            template.getTitle(),
                            template.format(pr.getId()),
                            NotificationType.PURCHASE_REQUEST_CONFIRMED,
                            pr.getId().toString(),
                            "/purchase-requests/" + pr.getId()
                    );
                }
            });

        } else {
            pr.setReviewStatus(ManagerReviewStatus.PENDING);
        }

        purchaseRequestRepo.save(pr);
    }


}
