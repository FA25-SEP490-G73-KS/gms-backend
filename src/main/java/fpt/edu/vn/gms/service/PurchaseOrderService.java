package fpt.edu.vn.gms.service;

import fpt.edu.vn.gms.dto.response.PurchaseOrderResponseDto;
import org.springframework.data.domain.Page;

public interface PurchaseOrderService {

    PurchaseOrderResponseDto createFromPurchaseRequest(Long requestId);

    PurchaseOrderResponseDto getById(Long id);

    Page<PurchaseOrderResponseDto> getAll(int page, int size);
}
