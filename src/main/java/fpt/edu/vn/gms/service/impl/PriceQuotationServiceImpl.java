package fpt.edu.vn.gms.service.impl;

import fpt.edu.vn.gms.common.enums.*;
import fpt.edu.vn.gms.dto.request.ChangeQuotationStatusReqDto;
import fpt.edu.vn.gms.dto.request.PriceQuotationItemRequestDto;
import fpt.edu.vn.gms.dto.request.PriceQuotationRequestDto;
import fpt.edu.vn.gms.dto.response.NotificationResponseDto;
import fpt.edu.vn.gms.dto.response.PriceQuotationResponseDto;
import fpt.edu.vn.gms.dto.response.ServiceTicketResponseDto;
import fpt.edu.vn.gms.entity.*;
import fpt.edu.vn.gms.exception.ResourceNotFoundException;
import fpt.edu.vn.gms.mapper.PriceQuotationMapper;
import fpt.edu.vn.gms.mapper.ServiceTicketMapper;
import fpt.edu.vn.gms.repository.*;
import fpt.edu.vn.gms.service.CodeSequenceService;
import fpt.edu.vn.gms.service.NotificationService;
import fpt.edu.vn.gms.service.PriceQuotationService;
import fpt.edu.vn.gms.service.StockExportService;
import fpt.edu.vn.gms.service.pdf.HtmlTemplateService;
import fpt.edu.vn.gms.service.pdf.PdfGeneratorService;
import fpt.edu.vn.gms.utils.NumberToVietnameseWordsUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class PriceQuotationServiceImpl implements PriceQuotationService {

    PriceQuotationRepository quotationRepository;
    ServiceTicketRepository serviceTicketRepository;
    PartRepository partRepository;
    PriceQuotationRepository priceQuotationRepository;
    NotificationService notificationService;
    CodeSequenceService codeSequenceService;
    HtmlTemplateService htmlTemplateService;
    PdfGeneratorService pdfGeneratorService;
    PriceQuotationMapper priceQuotationMapper;
    ServiceTicketMapper serviceTicketMapper;
    StockExportService stockExportService;

    @Override
    public Page<PriceQuotationResponseDto> findAllQuotations(Pageable pageable) {

        Page<PriceQuotation> quotations = quotationRepository.findAll(pageable);

        return quotations.map(priceQuotationMapper::toResponseDto);
    }

    @Transactional
    @Override
    public ServiceTicketResponseDto createQuotation(Long ticketId) {

        // Lấy service ticket theo id
        ServiceTicket serviceTicket = serviceTicketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phiếu dịch vụ với id = " + ticketId));

        // Tạo price quotation
        PriceQuotation quotation = PriceQuotation.builder()
                .code(codeSequenceService.generateCode("BG"))
                .status(PriceQuotationStatus.DRAFT)
                .estimateAmount(BigDecimal.ZERO)
                .build();

        quotation.setDiscount(serviceTicket.getCustomer().getDiscountPolicy().getDiscountRate());

        // Gán 2 chiều
        serviceTicket.setStatus(ServiceTicketStatus.WAITING_FOR_QUOTATION);

        quotation.setServiceTicket(serviceTicket);
        serviceTicket.setPriceQuotation(quotation);

        // Trả về DTO
        return serviceTicketMapper.toResponseDto(serviceTicketRepository.save(serviceTicket));
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
    public ServiceTicketResponseDto updateQuotationItems(Long quotationId, PriceQuotationRequestDto dto) {

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

        return serviceTicketMapper.toResponseDto(quotation.getServiceTicket());
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

        double quantity = dto.getQuantity() == null ? 1.0 : dto.getQuantity();
        item.setQuantity(quantity);

        item.setUnit(dto.getUnit());

        BigDecimal unitPrice = dto.getUnitPrice() == null ? BigDecimal.ZERO : dto.getUnitPrice();
        item.setUnitPrice(unitPrice);

        item.setTotalPrice(unitPrice.multiply(BigDecimal.valueOf(quantity)));
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

                boolean available = availableQty >= item.getQuantity();

                // AVAILABLE → inventory = AVAILABLE, review = CONFIRMED
                // OUT_OF_STOCK → inventory = OUT_OF_STOCK, review = PENDING
                item.setInventoryStatus(
                        available ? PriceQuotationItemStatus.AVAILABLE : PriceQuotationItemStatus.OUT_OF_STOCK);

                item.setWarehouseReviewStatus(
                        available ? WarehouseReviewStatus.CONFIRMED : WarehouseReviewStatus.PENDING);
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

        if (quotation.getPriceQuotationId() != null
                && quotation.getStatus() == PriceQuotationStatus.WAITING_CUSTOMER_CONFIRM) {
            quotation.setStatus(reqDto.getStatus());
            quotationRepository.save(quotation);
        } else {
            throw new RuntimeException("Không thể thay đổi trạng thái khi kho chưa xác nhận!!");
        }

        return priceQuotationMapper.toResponseDto(quotation);
    }

    @Transactional
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
            if (item.getItemType() != PriceQuotationItemType.PART)
                continue;

            if (item.getInventoryStatus() == PriceQuotationItemStatus.AVAILABLE) {
                availableParts.add(item);
            } else if (item.getInventoryStatus() == PriceQuotationItemStatus.OUT_OF_STOCK
                    || item.getInventoryStatus() == PriceQuotationItemStatus.UNKNOWN) {
                partsToBuy.add(item);
            }
        }

        for (PriceQuotationItem item : availableParts) {
            Part part = item.getPart();
            if (part == null)
                continue;

            // Cập nhật số lượng đã giữ
            double currentReserved = Optional.ofNullable(part.getReservedQuantity()).orElse(0.0);
            part.setReservedQuantity(currentReserved + item.getQuantity());
            partRepository.save(part);
        }

        quotationRepository.save(quotation);

        try {
            stockExportService.createExportFromQuotation(quotation.getPriceQuotationId(),
                    "Xuất kho theo báo giá đã được khách hàng xác nhận",
                    quotation.getServiceTicket().getCreatedBy());
        } catch (Exception e) {
            log.error("Không thể tạo phiếu xuất kho từ báo giá {}: {}", quotationId, e.getMessage());
        }

        NotificationTemplate template = NotificationTemplate.PRICE_QUOTATION_APPROVED;

        // Lấy nhân viên phụ trách (advisor)
        Employee advisor = quotation.getServiceTicket().getCreatedBy();

        String quotationCode = quotation.getCode();

        String formattedTitle = String.format(template.getTitle(), quotationCode);

        NotificationResponseDto notiDto = notificationService.createNotification(
                advisor.getEmployeeId(),
                formattedTitle,
                template.format(quotationCode),
                NotificationType.QUOTATION_CONFIRMED,
                quotation.getPriceQuotationId().toString(),
                "/service-tickets/" + quotation.getServiceTicket().getServiceTicketId());

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
        return quantityNeeded + minStock - availableStock;
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
            NotificationTemplate template = NotificationTemplate.PRICE_QUOTATION_REJECTED;

            String formattedTitle = String.format(template.getTitle(), quotation.getCode());

            NotificationResponseDto notificationDto = notificationService.createNotification(
                    advisor.getEmployeeId(),
                    formattedTitle,
                    template.format(quotation.getCode()),
                    NotificationType.QUOTATION_REJECTED,
                    quotation.getPriceQuotationId().toString(),
                    "/service-tickets/" + quotation.getServiceTicket().getServiceTicketId());
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

    @Override
    public long countWaitingCustomerConfirm() {
        return priceQuotationRepository.countByStatus(
                PriceQuotationStatus.WAITING_CUSTOMER_CONFIRM);
    }

    @Override
    public long countVehicleInRepairingStatus() {
        return priceQuotationRepository.countByStatus(
                PriceQuotationStatus.CUSTOMER_CONFIRMED);
    }

    @Override
    public PriceQuotationResponseDto updateLaborCost(Long id) {
        PriceQuotation quotation = priceQuotationRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("PriceQuotation not found id={}", id);
                    return new ResourceNotFoundException("Không tìm thấy báo giá!");
                });

        // Nếu bạn muốn update estimateAmount theo laborCost
        quotation.setEstimateAmount(
                quotation.getItems().stream()
                        .map(i -> i.getTotalPrice())
                        .reduce(BigDecimal.ZERO, BigDecimal::add));

        priceQuotationRepository.save(quotation);

        log.info("Updated laborCost successfully for quotationId={}", id);

        return priceQuotationMapper.toResponseDto(quotation);
    }

    @Override
    public byte[] exportPdfQuotation(Long quotationId) {

        ServiceTicket serviceTicket = serviceTicketRepository.findById(quotationId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy báo giá!"));

        ServiceTicketResponseDto ticket = serviceTicketMapper.toResponseDto(serviceTicket);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        DecimalFormat df = new DecimalFormat("#,###");

        Map<String, Object> data = new HashMap<>();
        data.put("date", ticket.getCreatedAt().format(formatter));
        data.put("quotationCode", ticket.getServiceTicketCode());
        data.put("customerName", ticket.getCustomer().getFullName());
        data.put("customerAddress", ticket.getCustomer().getAddress());
        data.put("licensePlate", ticket.getVehicle().getLicensePlate());
        data.put("carModel", ticket.getVehicle().getVehicleModelName());
        data.put("vin", ticket.getVehicle().getVin());
        data.put("engineNo", ticket.getVehicle().getVin());
        data.put("reason", ticket.getReceiveCondition());

        List<Map<String, Object>> items = getMaps(ticket);

        data.put("items", items);
        data.put("grandTotal", df.format(ticket.getPriceQuotation().getEstimateAmount()).replace(",", "."));

        data.put("grandTotalInWords",
                NumberToVietnameseWordsUtils.convert(ticket.getPriceQuotation().getEstimateAmount().longValue()));

        String html = htmlTemplateService.loadAndFillTemplate(
                "templates/quotation-template.html", data);

        return pdfGeneratorService.generateQuotationPdf(html);
    }

    @NotNull
    private static List<Map<String, Object>> getMaps(ServiceTicketResponseDto ticket) {
        List<Map<String, Object>> items = new ArrayList<>();

        DecimalFormat df = new DecimalFormat("#,###");

        int index = 1;
        for (var item : ticket.getPriceQuotation().getItems()) {
            Map<String, Object> row = new HashMap<>();
            row.put("index", index++);
            row.put("name",
                    item.getPart() != null
                            ? item.getPart().getName()
                            : item.getItemName());
            row.put("unit", item.getUnit());
            row.put("quantity", item.getQuantity());
            row.put("unitPrice", df.format(item.getUnitPrice()).replace(",", "."));
            row.put("total", df.format(item.getTotalPrice()).replace(",", "."));
            items.add(row);
        }
        return items;
    }
}
