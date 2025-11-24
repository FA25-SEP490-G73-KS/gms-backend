package fpt.edu.vn.gms.service;

import org.springframework.data.domain.Page;
import fpt.edu.vn.gms.dto.request.SupplierRequestDto;
import fpt.edu.vn.gms.dto.response.SupplierResponseDto;

public interface SupplierService {
    Page<SupplierResponseDto> getAllSuppliers(int page, int size);
    SupplierResponseDto getSupplierById(Long id);
    SupplierResponseDto createSupplier(SupplierRequestDto supplierRequestDto);
    SupplierResponseDto updateSupplier(Long id, SupplierRequestDto supplierRequestDto);
    void softDeleteSupplier(Long id);
}
