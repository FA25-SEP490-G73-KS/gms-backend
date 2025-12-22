package fpt.edu.vn.gms.dto.request;

import fpt.edu.vn.gms.common.enums.CustomerLoyaltyLevel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder

@Schema(description = "Thông tin khách hàng trong phiếu dịch vụ")
public class CustomerRequestDto {

     @Schema(description = "ID khách hàng (nullable nếu tạo mới)", example = "1")
     private Long customerId;

     @Schema(description = "Tên khách hàng", example = "Thành Nam", required = true)
     private String fullName;

     @Schema(description = "Số điện thoại", example = "84123456789", required = true)
     private String phone;

     @Schema(description = "Địa chỉ", example = "Hanoi")
     private String address;

     // Hạng khách hàng (BRONZE / SLIVER / GOLD) dùng để map sang
     // DiscountPolicy.loyaltyLevel
     private CustomerLoyaltyLevel customerLoyaltyLevel;
}
