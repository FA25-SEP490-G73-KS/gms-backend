package fpt.edu.vn.gms.service.impl;

import fpt.edu.vn.gms.common.Market;
import fpt.edu.vn.gms.common.PriceQuotationStatus;
import fpt.edu.vn.gms.common.PurchaseRequestStatus;
import fpt.edu.vn.gms.common.WarehouseReviewStatus;
import fpt.edu.vn.gms.dto.request.PartRequestDto;
import fpt.edu.vn.gms.dto.response.PurchaseRequestResponseDto;
import fpt.edu.vn.gms.entity.*;
import fpt.edu.vn.gms.exception.ResourceNotFoundException;
import fpt.edu.vn.gms.mapper.PurchaseRequestMapper;
import fpt.edu.vn.gms.repository.*;
import fpt.edu.vn.gms.service.PurchaseRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class PurchaseRequestServiceImpl implements PurchaseRequestService {

    private final PurchaseRequestRepository purchaseRequestRepository;
    private final PurchaseRequestItemRepository purchaseRequestItemRepository;
    private final VehicleModelRepository vehicleModelRepository;
    private final PartRepository partRepository;
    private final PriceQuotationRepository priceQuotationRepository;
    private final PriceQuotationItemRepository priceQuotationItemRepository;
    private final PurchaseRequestMapper purchaseRequestMapper;

    @Override
    public List<PurchaseRequestResponseDto> getAllPurchaseRequests() {
        List<PurchaseRequest> requests = purchaseRequestRepository.findAll();
        return purchaseRequestMapper.toResponseDtoList(requests);
    }

    @Override
    public PurchaseRequestResponseDto getPurchaseRequestById(Long id) {
        PurchaseRequest request = purchaseRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phiếu yêu cầu với id: " + id));
        return purchaseRequestMapper.toResponseDto(request);
    }

    @Override
    public PurchaseRequestResponseDto confirmPurchaseRequestItem(Long itemId, PartRequestDto partDto) {
        PurchaseRequestItem item = purchaseRequestItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy item với id: " + itemId));

        Part part = item.getPart();

        if (part == null) {
            part = new Part();
        }

        // Map dữ liệu từ DTO sang Part
        part.setName(partDto.getPartName());
        part.setMarket(Market.valueOf(partDto.getOrigin().toUpperCase())); // VD: "VN" → Market.VN
        part.setPurchasePrice(partDto.getImportPrice());
        part.setSellingPrice(partDto.getSalePrice());
        part.setQuantityInStock(partDto.getQuantity() != null ? partDto.getQuantity().doubleValue() : 0);
        part.setUniversal(Boolean.TRUE.equals(partDto.getIsUniversal()));

        // Map dòng xe
        if (partDto.getVehicleModelIds() != null && !partDto.getVehicleModelIds().isEmpty()) {
            Set<VehicleModel> models = new HashSet<>(vehicleModelRepository.findAllById(partDto.getVehicleModelIds()));
            part.setCompatibleVehicles(models);
        } else if (Boolean.TRUE.equals(partDto.getIsUniversal())) {
            part.setCompatibleVehicles(new HashSet<>());
        }

        partRepository.save(part);

        // Gắn lại vào item
        item.setPart(part);
        item.setStatus(WarehouseReviewStatus.CONFIRMED);

        purchaseRequestItemRepository.save(item);

        item.setStatus(WarehouseReviewStatus.CONFIRMED);
        purchaseRequestItemRepository.save(item);

        PurchaseRequest request = item.getPurchaseRequest();

        // Kiểm tra nếu toàn bộ item được confirm -> approved
        boolean allConfirmed = request.getItems()
                .stream()
                .allMatch(i -> i.getStatus() == WarehouseReviewStatus.CONFIRMED);

        if (allConfirmed) {
            request.setStatus(PurchaseRequestStatus.APPROVED);
            purchaseRequestRepository.save(request);

            // Cập nhật luôn PriceQuotation status
            PriceQuotation quotation = request.getItems().stream()
                    .map(PurchaseRequestItem::getQuotationItem)
                    .filter(Objects::nonNull)
                    .map(PriceQuotationItem::getPriceQuotation)
                    .filter(Objects::nonNull)
                    .findFirst()
                    .orElse(null);

            if (quotation != null) {
                quotation.setStatus(PriceQuotationStatus.CONFIRMED_BY_WAREHOUSE);
                priceQuotationRepository.save(quotation);
            }
        }

        return purchaseRequestMapper.toResponseDto(request);
    }

    @Override
    public PurchaseRequestResponseDto rejectPurchaseRequestItem(Long itemId, String note) {
        PurchaseRequestItem item = purchaseRequestItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy item với id: " + itemId));

        item.setStatus(WarehouseReviewStatus.REJECTED);
        item.setNote(note);
        purchaseRequestItemRepository.save(item);

        PriceQuotationItem quotationItem = item.getQuotationItem();
        if (quotationItem != null) {
            quotationItem.setWarehouseReviewStatus(WarehouseReviewStatus.REJECTED);
            priceQuotationItemRepository.save(quotationItem);
        }

        PurchaseRequest request = item.getPurchaseRequest();
        return purchaseRequestMapper.toResponseDto(request);
    }
}
