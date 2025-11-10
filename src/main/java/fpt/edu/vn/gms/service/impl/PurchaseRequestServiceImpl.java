package fpt.edu.vn.gms.service.impl;

import fpt.edu.vn.gms.common.*;
import fpt.edu.vn.gms.dto.response.PurchaseRequestResponseDto;
import fpt.edu.vn.gms.entity.*;
import fpt.edu.vn.gms.exception.ResourceNotFoundException;
import fpt.edu.vn.gms.mapper.PurchaseRequestMapper;
import fpt.edu.vn.gms.repository.*;
import fpt.edu.vn.gms.service.NotificationService;
import fpt.edu.vn.gms.service.PurchaseRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
public class PurchaseRequestServiceImpl implements PurchaseRequestService {

    private final PurchaseRequestRepository purchaseRequestRepo;
    private final PurchaseRequestItemRepository purchaseRequestItemRepo;
    private final EmployeeRepository employeeRepo;
    private final NotificationService notificationService;
    private final PurchaseRequestMapper purchaseRequestMapper;


    @Override
    public Page<PurchaseRequestResponseDto> getAllRequests(int page, int size) {

        Pageable pageable = PageRequest.of(page, size);

        return purchaseRequestRepo.findAll(pageable)
                .map(purchaseRequestMapper::toResponseDto);
    }

    @Transactional
    public void approveRequestItem(Long id, Long itemId) {
        PurchaseRequestItem pr = purchaseRequestItemRepo.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy PR"));
        pr.setStatus(PurchaseReqItemStatus.APPROVED);
        purchaseRequestItemRepo.save(pr);

        // Cập nhật trạng thái tổng thể PR
        updatePurchaseRequestStatus(id);
    }

    @Transactional
    public void rejectRequestItem(Long id, Long itemId, String reason) {
        PurchaseRequestItem item = purchaseRequestItemRepo.findById(itemId)
                .orElseThrow();
        item.setStatus(PurchaseReqItemStatus.REJECTED);
        purchaseRequestItemRepo.save(item);

        updatePurchaseRequestStatus(id);

        // Gửi notification cho service advisor
        String advisorPhone = item.getPurchaseRequest().getRelatedQuotation()
                .getServiceTicket()
                .getCreatedBy()
                .getPhone();

        notificationService.createNotification(
                advisorPhone,
                "Quản lý từ chối PR",
                "Item " + item.getPartName() + " trong PR #" +
                        item.getPurchaseRequest().getCode() + " đã bị từ chối.",
                NotificationType.QUOTATION_CONFIRMED
        );
    }

    private void updatePurchaseRequestStatus(Long id) {
        PurchaseRequest pr = purchaseRequestRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy PR"));

        List<PurchaseRequestItem> items = pr.getItems();

        boolean allApproved = items.stream()
                .allMatch(i -> i.getStatus() == PurchaseReqItemStatus.APPROVED);
        boolean isRejected = items.stream()
                .anyMatch(i -> i.getStatus() == PurchaseReqItemStatus.REJECTED);

        if (allApproved) {
            pr.setStatus(PurchaseRequestStatus.APPROVED);
        } else if (isRejected) {
            pr.setStatus(PurchaseRequestStatus.REJECTED);
        }

        purchaseRequestRepo.save(pr);

        String title;
        String message;

        if (allApproved) {
            title = "Phiếu yêu cầu mua hàng được phê duyệt";
            message = "PR #" + pr.getCode() + " đã được phê duyệt.";
        } else if (isRejected) {
            title = "Phiếu yêu cầu mua hàng bị từ chối";
            message = "PR #" + pr.getCode() + " đã bị từ chối.";
        } else {
            return;
        }

        // 1. Gửi cho tất cả nhân viên kho
        List<Employee> warehouseStaff = employeeRepo.findByEmployeeRole(EmployeeRole.WAREHOUSE);
        for (Employee staff : warehouseStaff) {
            notificationService.createNotification(
                    staff.getPhone(),
                    title,
                    message,
                    NotificationType.PURCHASE_REQUEST_UPDATED
            );
        }

        // 2. Gửi CC cho cố vấn dịch vụ
        ServiceTicket st = pr.getRelatedServiceTicket();
        if (st != null && st.getCreatedBy() != null) {
            Employee advisor = st.getCreatedBy();
            notificationService.createNotification(
                    advisor.getPhone(),
                    "[CC] " + title,
                    message,
                    NotificationType.PURCHASE_REQUEST_UPDATED
            );
        }
    }


}
