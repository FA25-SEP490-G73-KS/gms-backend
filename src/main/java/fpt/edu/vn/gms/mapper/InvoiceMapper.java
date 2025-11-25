package fpt.edu.vn.gms.mapper;

import fpt.edu.vn.gms.dto.response.InvoiceDetailResDto;
import fpt.edu.vn.gms.dto.response.InvoiceListResDto;
import fpt.edu.vn.gms.entity.Invoice;
import fpt.edu.vn.gms.utils.NumberToVietnameseWordsUtils;

import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = { PriceQuotationItemMapper.class, ServiceTicketMapper.class })
public interface InvoiceMapper {

    @Mapping(source = "serviceTicket.serviceTicketCode", target = "serviceTicketCode")
    @Mapping(source = "serviceTicket.customer.fullName", target = "customerName")
    @Mapping(source = "serviceTicket.vehicle.licensePlate", target = "licensePlate")
    InvoiceListResDto toListDto(Invoice payment);

    @Mapping(target = "serviceTicket", source = "serviceTicket", qualifiedByName = "toServiceTicketResponseDto")
    @Mapping(target = "customerName", source = "serviceTicket.customerName")
    @Mapping(target = "items", source = "quotation.items")
    @Mapping(target = "totalItemPrice", source = "itemTotal")
    // Amount in words (nếu bạn tự thêm sau)
    @Mapping(target = "amountInWords", ignore = true)
    InvoiceDetailResDto toDetailDto(Invoice payment);

    @AfterMapping
    default void fillAmountInWords(Invoice payment, @MappingTarget InvoiceDetailResDto.InvoiceDetailResDtoBuilder dto) {
        dto.amountInWords(NumberToVietnameseWordsUtils.convert(payment.getItemTotal().longValue()));
    }
}
