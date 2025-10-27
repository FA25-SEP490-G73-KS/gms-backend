package fpt.edu.vn.gms.service.impl;

import fpt.edu.vn.gms.common.ServiceTicketStatus;
import fpt.edu.vn.gms.dto.request.PriceQuotationItemRequestDto;
import fpt.edu.vn.gms.dto.request.PriceQuotationRequestDto;
import fpt.edu.vn.gms.dto.response.PriceQuotationResponseDto;
import fpt.edu.vn.gms.entity.Part;
import fpt.edu.vn.gms.entity.PriceQuotation;
import fpt.edu.vn.gms.entity.PriceQuotationItem;
import fpt.edu.vn.gms.entity.ServiceTicket;
import fpt.edu.vn.gms.exception.ResourceNotFoundException;
import fpt.edu.vn.gms.mapper.PriceQuotationMapper;
import fpt.edu.vn.gms.repository.PartRepository;
import fpt.edu.vn.gms.repository.PriceQuotationItemRepository;
import fpt.edu.vn.gms.repository.PriceQuotationRepository;
import fpt.edu.vn.gms.repository.ServiceTicketRepository;
import fpt.edu.vn.gms.service.PriceQuotationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PriceQuotationServiceImpl implements PriceQuotationService {

    private final ServiceTicketRepository serviceTicketRepository;
    private final PriceQuotationRepository priceQuotationRepository;
    private final PriceQuotationItemRepository priceQuotationItemRepository;
    private final PartRepository partRepository;
    private final PriceQuotationMapper priceQuotationMapper;

    @Override
    public PriceQuotationResponseDto createQuotationFromServiceTicket(Long id, PriceQuotationRequestDto request) {

        // Lấy phiếu dịch vụ
        ServiceTicket ticket = serviceTicketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phiếu dịch vụ."));

        // Kiểm tra trạng thái hợp lệ
        if (ticket.getStatus() != ServiceTicketStatus.TIEP_NHAN
                && ticket.getStatus() != ServiceTicketStatus.DANG_BAO_GIA) {
            throw new IllegalStateException("Phiếu dịch vụ không ở trạng thái cho phép tạo báo giá.");
        }

        // Tạo phiếu báo giá mới
        PriceQuotation quotation = PriceQuotation.builder()
                .serviceTicket(ticket)
                .estimateAmount(BigDecimal.ZERO)
                .discount(BigDecimal.ZERO)
                .build();

        priceQuotationRepository.save(quotation);

        // Thêm các item
        BigDecimal total = BigDecimal.ZERO;
        List<PriceQuotationItem> itemList = new ArrayList<>();

        for (PriceQuotationItemRequestDto itemDto : request.getItems()) {
            Part part = null;
            if (itemDto.getPartId() != null) {
                part = partRepository.findById(itemDto.getPartId())
                        .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy linh kiện"));
            }

            PriceQuotationItem item = PriceQuotationItem.builder()
                    .priceQuotation(quotation)
                    .part(part)
                    .partName(itemDto.getPartName())
                    .unitPrice(itemDto.getUnitPrice())
                    .quantity(itemDto.getQuantity())
                    .discountRate(itemDto.getDiscount() != null ? itemDto.getDiscount() : BigDecimal.ZERO)
                    .description(itemDto.getDescription())
                    .build();

            item.syncStatusWithPart();  // tự set ACTIVE / TEMPORARY / OUT_OF_STOCK
            item.calculateTotal();

            total = total.add(item.getTotalPrice());
            itemList.add(item);
        }

        priceQuotationItemRepository.saveAll(itemList);

        // Cập nhật tổng tiền
        quotation.setEstimateAmount(total);
        priceQuotationRepository.save(quotation);

        // Cập nhật trạng thái phiếu dịch vụ
        ticket.setPriceQuotation(quotation);
        ticket.setStatus(ServiceTicketStatus.CHO_DUYET_BAO_GIA);
        serviceTicketRepository.save(ticket);

        return priceQuotationMapper.toResponseDto(quotation);

    }
}
