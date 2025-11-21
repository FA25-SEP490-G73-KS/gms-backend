package fpt.edu.vn.gms.mapper;

import fpt.edu.vn.gms.dto.response.PaymentDetailResDto;
import fpt.edu.vn.gms.dto.response.PaymentListResDto;
import fpt.edu.vn.gms.entity.Payment;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {PriceQuotationItemMapper.class})
public interface PaymentMapper {

    @Mapping(source = "serviceTicket.serviceTicketCode", target = "serviceTicketCode")
    @Mapping(source = "serviceTicket.customer.fullName", target = "customerName")
    @Mapping(source = "serviceTicket.vehicle.licensePlate", target = "licensePlate")
    @Mapping(target = "previousDebt", ignore = true)
    PaymentListResDto toListDto(Payment payment);

    @Mappings({
            @Mapping(target = "paymentId", source = "id"),
            @Mapping(target = "paymentCode", source = "code"),

            // Service Ticket
            @Mapping(target = "serviceTicketId", source = "serviceTicket.serviceTicketId"),
            @Mapping(target = "customerName", source = "serviceTicket.customerName"),

            // Items mapping
            @Mapping(target = "items", source = "quotation.items"),

            // Payment summary
            @Mapping(target = "totalItemPrice", source = "itemTotal"),

            // Amount in words (nếu bạn tự thêm sau)
            @Mapping(target = "amountInWords", ignore = true)
    })
    PaymentDetailResDto toDetailDto(Payment payment);

    @AfterMapping
    default void fillAmountInWords(Payment payment, @MappingTarget PaymentDetailResDto.PaymentDetailResDtoBuilder dto) {
        // TODO: convert payment.getFinalAmount() -> words
        dto.amountInWords("..."); // placeholder
    }
}
