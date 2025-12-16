package fpt.edu.vn.gms.mapper;

import fpt.edu.vn.gms.dto.response.PartReqDto;
import fpt.edu.vn.gms.entity.Part;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PartMapper {

    // Map đầy đủ tất cả field cần cho PartReqDto
    @Mapping(target = "partId", source = "partId")
    @Mapping(target = "sku", source = "sku")
    @Mapping(target = "name", source = "name")

    // Danh mục
    @Mapping(target = "categoryId", source = "category.id")
    @Mapping(target = "categoryName", source = "category.name")

    // Hãng xe + mẫu xe (thông qua vehicleModel)
    @Mapping(target = "brandId", source = "vehicleModel.brand.brandId")
    @Mapping(target = "brandName", source = "vehicleModel.brand.name")
    @Mapping(target = "modelId", source = "vehicleModel.vehicleModelId")
    @Mapping(target = "modelName", source = "vehicleModel.name")

    // Thị trường
    @Mapping(target = "marketId", source = "market.id")
    @Mapping(target = "marketName", source = "market.name")

    // Cờ dùng chung & đặc biệt
    @Mapping(target = "universal", source = "universal")
    @Mapping(target = "specialPart", source = "specialPart")

    // Nhà cung cấp
    @Mapping(target = "supplierId", source = "supplier.id")
    @Mapping(target = "supplierName", source = "supplier.name")

    // Giá
    @Mapping(target = "purchasePrice", source = "purchasePrice")
    @Mapping(target = "sellingPrice", source = "sellingPrice")
    @Mapping(target = "discountRate", source = "discountRate")

    // Số lượng + đơn vị
    @Mapping(target = "quantity", source = "quantityInStock")
    @Mapping(target = "unitId", source = "unit.id")
    @Mapping(target = "unitName", source = "unit.name")
    @Mapping(target = "reservedQuantity", source = "reservedQuantity")
    @Mapping(target = "reorderLevel", source = "reorderLevel")

    // Ghi chú + trạng thái tồn kho
    @Mapping(target = "note", source = "note")
    @Mapping(target = "status", source = "status")
    PartReqDto toDto(Part part);

}
