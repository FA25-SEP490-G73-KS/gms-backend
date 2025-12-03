package fpt.edu.vn.gms.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ManualTransactionItemRequest {
    private Long partId;
    private Double quantity;
    private String unit;        // dùng để hiển thị, không ảnh hưởng tới kho
    private BigDecimal price;   // đơn giá, chỉ dùng cho RECEIPT
    private BigDecimal totalPrice; // tổng tiền dòng, FE tính và gửi lên
    private String note;
}
