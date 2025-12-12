package fpt.edu.vn.gms.mapper;

import fpt.edu.vn.gms.dto.response.ServiceRatingResponse;
import fpt.edu.vn.gms.entity.ServiceRating;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ServiceRatingMapper {

    @Mapping(target = "serviceTicketId", source = "serviceTicket.serviceTicketId")
    @Mapping(target = "customerId", source = "customer.customerId")
    ServiceRatingResponse toDto(ServiceRating rating);
}

