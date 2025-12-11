package fpt.edu.vn.gms.service.impl;

import fpt.edu.vn.gms.entity.Supplier;
import fpt.edu.vn.gms.exception.ResourceNotFoundException;
import fpt.edu.vn.gms.repository.SupplierRepository;
import fpt.edu.vn.gms.service.SupplierService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import fpt.edu.vn.gms.dto.request.SupplierRequestDto;
import fpt.edu.vn.gms.dto.response.SupplierResponseDto;
import fpt.edu.vn.gms.mapper.SupplierMapper;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class SupplierServiceImpl implements SupplierService {
    SupplierRepository supplierRepository;
    SupplierMapper supplierMapper;

    @Override
    public Page<SupplierResponseDto> getAllSuppliers(int page, int size) {
        log.info("Lấy danh sách nhà cung cấp, page={}, size={}", page, size);
        return supplierRepository.findAll(PageRequest.of(page, size))
                .map(supplierMapper::toResponseDto);
    }

    @Override
    public SupplierResponseDto getSupplierById(Long id) {
        log.info("Lấy thông tin nhà cung cấp với id={}", id);
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with id: " + id));
        return supplierMapper.toResponseDto(supplier);
    }

    @Override
    public SupplierResponseDto createSupplier(SupplierRequestDto dto) {
        log.info("Tạo mới nhà cung cấp: {}", dto.getName());
        Supplier supplier = supplierMapper.toEntity(dto);
        supplier.setIsActive(true);
        Supplier saved = supplierRepository.save(supplier);
        return supplierMapper.toResponseDto(saved);
    }

    @Override
    public SupplierResponseDto updateSupplier(Long id, SupplierRequestDto dto) {
        log.info("Cập nhật nhà cung cấp id={}", id);
        Supplier existing = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with id: " + id));
        supplierMapper.updateSupplierFromDto(dto, existing);
        Supplier updated = supplierRepository.save(existing);
        return supplierMapper.toResponseDto(updated);
    }

    @Override
    public SupplierResponseDto toggleSupplierActiveStatus(Long id) {
        log.info("Toggle trạng thái nhà cung cấp id={}", id);
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Supplier not found with id: " + id));
        supplier.setIsActive(!supplier.getIsActive());
        Supplier updated = supplierRepository.save(supplier);
        return supplierMapper.toResponseDto(updated);
    }
}
