package fpt.edu.vn.gms.service.impl;

import fpt.edu.vn.gms.dto.response.PurchaseRequestItemDetailDto;
import fpt.edu.vn.gms.repository.PurchaseRequestItemRepository;
import fpt.edu.vn.gms.service.PurchaseRequestItemService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PurchaseRequestItemServiceImpl implements PurchaseRequestItemService {

    PurchaseRequestItemRepository purchaseRequestItemRepository;

    @Override
    public List<PurchaseRequestItemDetailDto> getItemsByPurchaseRequestId(Long purchaseRequestId) {

        return purchaseRequestItemRepository.findItemsByPurchaseRequestId(purchaseRequestId);
    }
}
