package fpt.edu.vn.gms.dto.request;

import lombok.Data;
import java.util.List;

@Data
public class CreatePurchaseRequestFromQuotationDto {
    private List<Long> quotationIds; // Đổi từ quotationId thành List để hỗ trợ nhiều quotations
    private List<Long> quotationItemIds;
    private String reason;
    private String note;
}
