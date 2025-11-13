package fpt.edu.vn.gms.service.impl;

import fpt.edu.vn.gms.common.*;
import fpt.edu.vn.gms.dto.request.ChangeQuotationStatusReqDto;
import fpt.edu.vn.gms.dto.request.PriceQuotationItemRequestDto;
import fpt.edu.vn.gms.dto.request.PriceQuotationRequestDto;
import fpt.edu.vn.gms.dto.response.NotificationResponseDto;
import fpt.edu.vn.gms.dto.response.PriceQuotationResponseDto;
import fpt.edu.vn.gms.entity.*;
import fpt.edu.vn.gms.exception.ResourceNotFoundException;
import fpt.edu.vn.gms.mapper.PriceQuotationMapper;
import fpt.edu.vn.gms.repository.*;
import fpt.edu.vn.gms.service.CodeSequenceService;
import fpt.edu.vn.gms.service.NotificationService;
import fpt.edu.vn.gms.service.PriceQuotationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class PriceQuotationServiceImpl implements PriceQuotationService {

    private final PriceQuotationRepository quotationRepository;
    private final ServiceTicketRepository serviceTicketRepository;
    private final PartRepository partRepository;
    private final PurchaseRequestRepository purchaseRequestRepository;
    private final PartReservationRepository partReservationRepository;
    private final NotificationSocketService notificationSocketService;
    private final NotificationService notificationService;
    private final CodeSequenceService codeSequenceService;
    private final PriceQuotationMapper priceQuotationMapper;

    @Override
    public PriceQuotationResponseDto createQuotation(Long ticketId) {

        // Lấy service ticket theo id
        ServiceTicket serviceTicket = serviceTicketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phiếu dịch vụ với id = " + ticketId));

        // Tạo price quotation
        PriceQuotation quotation = PriceQuotation.builder()
                .status(PriceQuotationStatus.DRAFT)
                .estimateAmount(BigDecimal.ZERO)
                .build();

        // Gán 2 chiều
        quotation.setServiceTicket(serviceTicket);
        serviceTicket.setPriceQuotation(quotation);

        // Lưu (vì cascade = ALL nên chỉ cần save ticket)
        serviceTicketRepository.save(serviceTicket);

        // Trả về DTO
        return priceQuotationMapper.toResponseDto(quotation);
    }

    @Override
    public PriceQuotationResponseDto updateQuotationItems(Long quotationId, PriceQuotationRequestDto dto) {

        PriceQuotation quotation = quotationRepository.findById(quotationId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy báo giá ID: " + quotationId));

        quotation.setUpdatedAt(LocalDateTime.now());
        List<PriceQuotationItem> existingItems = quotation.getItems();

        for (var itemDto : dto.getItems()) {
            PriceQuotationItem item = existingItems.stream()
                    .filter(i -> i.getPriceQuotationItemId() != null &&
                            i.getPriceQuotationItemId().equals(itemDto.getPriceQuotationItemId()))
                    .findFirst()
                    .orElse(null);

            if (item != null) {
                applyItemUpdates(item, itemDto);
            } else {
                PriceQuotationItem newItem = new PriceQuotationItem();
                newItem.setPriceQuotation(quotation);
                applyItemUpdates(newItem, itemDto);
                existingItems.add(newItem);
            }
        }

        quotation.setEstimateAmount(dto.getEstimateAmount());

        // --- Tự động check trạng thái ---
        updateQuotationStatusAfterItemUpdate(quotation);

        quotationRepository.save(quotation);

        return priceQuotationMapper.toResponseDto(quotation);
    }

    private void updateQuotationStatusAfterItemUpdate(PriceQuotation quotation) {
        List<PriceQuotationItem> partItems = quotation.getItems().stream()
                .filter(i -> i.getItemType() == PriceQuotationItemType.PART)
                .toList();

        // Kiểm tra xem tất cả item PART đã sẵn sàng gửi khách hay vẫn cần kho xác nhận
        boolean allReady = partItems.stream()
                .allMatch(i -> i.getInventoryStatus() == PriceQuotationItemStatus.AVAILABLE
                        || i.getWarehouseReviewStatus() == WarehouseReviewStatus.CONFIRMED);

        if (allReady) {
            quotation.setStatus(PriceQuotationStatus.WAREHOUSE_CONFIRMED);
        } else {
            quotation.setStatus(PriceQuotationStatus.WAITING_WAREHOUSE_CONFIRM);
        }

        quotation.setUpdatedAt(LocalDateTime.now());
        quotationRepository.save(quotation);
    }


    @Override
    public PriceQuotationResponseDto getById(Long id) {
        return priceQuotationMapper.toResponseDto(quotationRepository.findById(id).orElse(null));
    }


    private void applyItemUpdates(PriceQuotationItem item, PriceQuotationItemRequestDto dto) {
        item.setItemName(dto.getItemName());
        item.setItemType(dto.getType());
        item.setQuantity(dto.getQuantity());
        item.setUnit(dto.getUnit());
        item.setUnitPrice(dto.getUnitPrice());
        item.setTotalPrice(dto.getUnitPrice().multiply(BigDecimal.valueOf(dto.getQuantity())));
        item.setWarehouseNote(null);

        // Xử lý riêng cho PART
        if (dto.getType() == PriceQuotationItemType.PART) {
            Part part = null;

            if (dto.getPartId() != null) {
                part = partRepository.findById(dto.getPartId())
                        .orElseThrow(() -> new ResourceNotFoundException(
                                "Không tìm thấy linh kiện ID: " + dto.getPartId()));
                item.setPart(part);
            }

            if (part == null) {
                item.setInventoryStatus(PriceQuotationItemStatus.UNKNOWN);;
            } else {
                double availableQty = Optional.ofNullable(part.getQuantityInStock()).orElse(0.0)
                        - Optional.ofNullable(part.getReservedQuantity()).orElse(0.0);
                item.setInventoryStatus(
                        availableQty >= item.getQuantity()
                                ? PriceQuotationItemStatus.AVAILABLE
                                : PriceQuotationItemStatus.OUT_OF_STOCK
                );
                item.setWarehouseReviewStatus(WarehouseReviewStatus.CONFIRMED);
            }
        } else {
            item.setPart(null);
            item.setInventoryStatus(null);
        }
    }



    @Override
    public PriceQuotationResponseDto updateQuotationStatusManual(Long id, ChangeQuotationStatusReqDto reqDto) {

        PriceQuotation quotation = quotationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Quotation không tồn tại!!!"));

        if (quotation.getPriceQuotationId() != null && quotation.getStatus() == PriceQuotationStatus.WAITING_CUSTOMER_CONFIRM) {
            quotation.setStatus(reqDto.getStatus());
            quotationRepository.save(quotation);
        } else {
            throw new RuntimeException("Không thể thay đổi trạng thái khi kho chưa xác nhận!!");
        }

        return priceQuotationMapper.toResponseDto(quotation);
    }

    @Override
    public PriceQuotationResponseDto confirmQuotationByCustomer(Long quotationId) {

        PriceQuotation quotation = quotationRepository.findById(quotationId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy báo giá ID: " + quotationId));

        if (quotation.getStatus() != PriceQuotationStatus.WAITING_CUSTOMER_CONFIRM) {
            throw new RuntimeException("Chỉ có thể xác nhận khi đang chờ khách hàng xác nhận");
        }

        // Cập nhật trạng thái báo giá
        quotation.setStatus(PriceQuotationStatus.CUSTOMER_CONFIRMED);
        quotation.setUpdatedAt(LocalDateTime.now());

        // Lấy danh sách item theo từng loại tồn kho
        List<PriceQuotationItem> availableParts = new ArrayList<>();
        List<PriceQuotationItem> partsToBuy = new ArrayList<>();

        for (PriceQuotationItem item : quotation.getItems()) {
            if (item.getItemType() != PriceQuotationItemType.PART) continue;

            if (item.getInventoryStatus() == PriceQuotationItemStatus.AVAILABLE) {
                availableParts.add(item);
            } else if (item.getInventoryStatus() == PriceQuotationItemStatus.OUT_OF_STOCK
                    || item.getInventoryStatus() == PriceQuotationItemStatus.UNKNOWN) {
                partsToBuy.add(item);
            }
        }

        // 1. Tạo PartReservation cho các linh kiện AVAILABLE
        for (PriceQuotationItem item : availableParts) {
            Part part = item.getPart();
            if (part == null) continue;

            // Tạo bản ghi đặt giữ
            PartReservation reservation = PartReservation.builder()
                    .part(part)
                    .quotationItem(item)
                    .reservedQuantity(item.getQuantity())
                    .reservedAt(LocalDateTime.now())
                    .active(true)
                    .build();
            partReservationRepository.save(reservation);

            // Cập nhật số lượng đã giữ
            double currentReserved = Optional.ofNullable(part.getReservedQuantity()).orElse(0.0);
            part.setReservedQuantity(currentReserved + item.getQuantity());
            partRepository.save(part);
        }

        // 2. Tạo PurchaseRequest cho OUT_OF_STOCK và UNKNOWN
        BigDecimal totalEstimatedAmount = BigDecimal.ZERO;

        if (!partsToBuy.isEmpty()) {
            PurchaseRequest purchaseRequest = PurchaseRequest.builder()
                    .code(codeSequenceService.generateCode("PR"))
                    .relatedQuotation(quotation)
                    .status(PurchaseRequestStatus.PENDING)
                    .createdAt(LocalDateTime.now())
                    .totalEstimatedAmount(totalEstimatedAmount)
                    .createdBy(null)
                    .items(new ArrayList<>())
                    .build();


            for (PriceQuotationItem item : partsToBuy) {
                PurchaseRequestItem requestItem = PurchaseRequestItem.builder()
                        .part(item.getPart())
                        .partName(item.getItemName())
                        .quantity(item.getQuantity())
                        .unit(item.getUnit())
                        .estimatedPurchasePrice(item.getPart().getPurchasePrice())
                        .status(PurchaseReqItemStatus.PENDING)
                        .purchaseRequest(purchaseRequest)
                        .build();

                purchaseRequest.getItems().add(requestItem);

                // Cộng dồn vào tổng
                totalEstimatedAmount = totalEstimatedAmount.add(requestItem.getEstimatedPurchasePrice());
            }

            purchaseRequest.setTotalEstimatedAmount(totalEstimatedAmount);
            purchaseRequestRepository.save(purchaseRequest);
        }

        // Lưu lại báo giá
        quotationRepository.save(quotation);

        // --- Gửi realtime WebSocket trước khi lưu Notification vào DB ---
        String advisorPhone = quotation.getServiceTicket().getCreatedBy().getPhone();
        NotificationResponseDto wsNotification = NotificationResponseDto.builder()
                .title("Khách hàng đã đồng ý phiếu dịch vụ")
                .message(String.format("Khách hàng đồng ý phiếu dịch vụ #%s", quotation.getServiceTicket().getServiceTicketCode()))
                .type(NotificationType.QUOTATION_CONFIRMED)
                .build();

        notificationSocketService.sendToAdvisor(advisorPhone, wsNotification);

        // --- Lưu notification vào DB ---
        notificationService.createNotification(
                advisorPhone,
                wsNotification.getTitle(),
                wsNotification.getMessage(),
                wsNotification.getType()
        );

        return priceQuotationMapper.toResponseDto(quotation);
    }

    public PriceQuotationResponseDto rejectQuotationByCustomer(Long quotationId, String reason) {
        PriceQuotation quotation = quotationRepository.findById(quotationId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy báo giá ID: " + quotationId));

        if (quotation.getStatus() != PriceQuotationStatus.WAITING_CUSTOMER_CONFIRM) {
            throw new RuntimeException("Chỉ có thể từ chối khi đang chờ khách xác nhận");
        }

        quotation.setStatus(PriceQuotationStatus.CUSTOMER_REJECTED);
        quotation.setUpdatedAt(LocalDateTime.now());
        quotation.setRejectReason(reason);

        quotationRepository.save(quotation);

        String advisorPhone = quotation.getServiceTicket().getCreatedBy().getPhone();

        // Gửi realtime WebSocket
        NotificationResponseDto wsNotification = NotificationResponseDto.builder()
                .title("Khách hàng từ chối phiếu dịch vụ")
                .message(String.format("Khách hàng từ chối phiếu dịch vụ #%s", quotation.getServiceTicket().getServiceTicketCode()))
                .type(NotificationType.QUOTATION_REJECTED)
                .code(quotation.getServiceTicket().getServiceTicketCode())
                .build();

        notificationSocketService.sendToAdvisor(advisorPhone, wsNotification);

        // Lưu notification vào DB
        notificationService.createNotification(
                advisorPhone,
                wsNotification.getTitle(),
                wsNotification.getMessage(),
                wsNotification.getType()
        );

        return priceQuotationMapper.toResponseDto(quotation);
    }

    @Override
    public PriceQuotationResponseDto sendQuotationToCustomer(Long quotationId) {

        PriceQuotation quotation = quotationRepository.findById(quotationId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy báo giá ID: " + quotationId));

        if (quotation.getStatus() == PriceQuotationStatus.WAITING_WAREHOUSE_CONFIRM) {
            throw new RuntimeException("Chỉ có thể gửi khi kho đã xác nhận báo giá");
        }

        quotation.setStatus(PriceQuotationStatus.WAITING_CUSTOMER_CONFIRM);
        quotation.setUpdatedAt(LocalDateTime.now());

        quotationRepository.save(quotation);

        return priceQuotationMapper.toResponseDto(quotation);
    }
}
