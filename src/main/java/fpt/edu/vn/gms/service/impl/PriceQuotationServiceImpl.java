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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    private final NotificationService notificationService;
    private final CodeSequenceService codeSequenceService;
    private final PriceQuotationMapper priceQuotationMapper;

    @Override
    public Page<PriceQuotationResponseDto> findAllQuotations(Pageable pageable) {

        Page<PriceQuotation> quotations = quotationRepository.findAll(pageable);

        return quotations.map(priceQuotationMapper::toResponseDto);
    }

    @Override
    public PriceQuotationResponseDto createQuotation(Long ticketId) {

        // Lấy service ticket theo id
        ServiceTicket serviceTicket = serviceTicketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phiếu dịch vụ với id = " + ticketId));

        // Tạo price quotation
        PriceQuotation quotation = PriceQuotation.builder()
                .code(codeSequenceService.generateCode("QT"))
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
    public PriceQuotationResponseDto recalculateEstimateAmount(Long quotationId) {
        PriceQuotation quotation = quotationRepository.findById(quotationId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy báo giá ID: " + quotationId));

        // Tính toán lại tổng dự kiến dựa trên items
        BigDecimal totalEstimate = quotation.getItems().stream()
                .map(item -> Optional.ofNullable(item.getTotalPrice()).orElse(BigDecimal.ZERO))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        quotation.setEstimateAmount(totalEstimate);
        quotation.setUpdatedAt(LocalDateTime.now());

        quotationRepository.save(quotation);

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
                item.setInventoryStatus(PriceQuotationItemStatus.UNKNOWN);
                item.setWarehouseReviewStatus(WarehouseReviewStatus.PENDING);
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
            item.setWarehouseReviewStatus(WarehouseReviewStatus.CONFIRMED);
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

            item.setExportStatus(ExportStatus.WAITING_TO_EXPORT);
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
                    .createdBy(null) // Hệ thống tự tạo
                    .items(new ArrayList<>())
                    .build();


            for (PriceQuotationItem item : partsToBuy) {

                item.setExportStatus(ExportStatus.WAITING_PURCHASE);

                double quantityToPurchase = getQuantityToPurchase(item);

                PurchaseRequestItem requestItem = PurchaseRequestItem.builder()
                        .part(item.getPart())
                        .partName(item.getItemName())
                        .quantity(quantityToPurchase)
                        .unit(item.getUnit())
                        .estimatedPurchasePrice(item.getPart().getPurchasePrice().multiply(BigDecimal.valueOf(quantityToPurchase)))
                        .status(PurchaseReqItemStatus.PENDING)
                        .purchaseRequest(purchaseRequest)
                        .build();

                purchaseRequest.getItems().add(requestItem);

                // Cộng dồn vào tổng
                BigDecimal lineTotal = requestItem.getEstimatedPurchasePrice()
                        .multiply(BigDecimal.valueOf(item.getQuantity()));

                totalEstimatedAmount = totalEstimatedAmount.add(lineTotal);

            }

            purchaseRequest.setTotalEstimatedAmount(totalEstimatedAmount);
            purchaseRequestRepository.save(purchaseRequest);
        }

        quotation.setExportStatus(ExportStatus.WAITING_TO_EXPORT);

        // Lưu lại báo giá
        quotationRepository.save(quotation);

        NotificationTemplate template = NotificationTemplate.PRICE_QUOTATION_APPROVED;

        // Lấy nhân viên phụ trách (advisor)
        Employee advisor = quotation.getServiceTicket().getCreatedBy();

        NotificationResponseDto notiDto = notificationService.createNotification(
                advisor.getEmployeeId(),
                template.getTitle(),
                template.format(quotation.getPriceQuotationId()),
                NotificationType.QUOTATION_CONFIRMED,
                quotation.getPriceQuotationId().toString(),
                "/service-tickets/" + quotation.getServiceTicket().getServiceTicketId()
        );

        return priceQuotationMapper.toResponseDto(quotation);
    }

    private static double getQuantityToPurchase(PriceQuotationItem item) {
        double quantityNeeded = item.getQuantity();
        Part part = item.getPart();

        double quantityInStock = Optional.ofNullable(part.getQuantityInStock()).orElse(0.0);
        double reservedQty = Optional.ofNullable(part.getReservedQuantity()).orElse(0.0);
        double minStock = Optional.ofNullable(part.getReorderLevel()).orElse(0.0);

        // Số lượng thực sự có thể dùng
        double availableStock = quantityInStock - reservedQty;

        // Số lượng cần PR
        double quantityToPurchase = quantityNeeded + minStock - availableStock;
        return quantityToPurchase;
    }

    @Override
    public PriceQuotationResponseDto rejectQuotationByCustomer(Long quotationId, String reason) {

        // Lấy báo giá
        PriceQuotation quotation = quotationRepository.findById(quotationId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy báo giá ID: " + quotationId));

        if (quotation.getStatus() != PriceQuotationStatus.WAITING_CUSTOMER_CONFIRM) {
            throw new RuntimeException("Chỉ có thể từ chối khi đang chờ khách xác nhận");
        }

        // Cập nhật trạng thái báo giá
        quotation.setStatus(PriceQuotationStatus.CUSTOMER_REJECTED);
        quotation.setUpdatedAt(LocalDateTime.now());

        quotationRepository.save(quotation);

        // Lấy nhân viên phụ trách (advisor)
        Employee advisor = quotation.getServiceTicket().getCreatedBy();
        if (advisor != null) {
            // Sử dụng NotificationTemplate
            NotificationTemplate template = NotificationTemplate.PRICE_QUOTATION_REJECTED;

            NotificationResponseDto notificationDto = notificationService.createNotification(
                    advisor.getEmployeeId(),
                    template.getTitle(),
                    template.format(quotation.getPriceQuotationId()),
                    NotificationType.QUOTATION_REJECTED,
                    quotation.getPriceQuotationId().toString(),
                    "/service-tickets/" + quotation.getServiceTicket().getServiceTicketId()
            );

            // WebSocket realtime đã được push trong createNotification
        }

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
