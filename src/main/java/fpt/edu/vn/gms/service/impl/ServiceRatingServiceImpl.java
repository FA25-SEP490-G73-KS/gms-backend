package fpt.edu.vn.gms.service.impl;

import fpt.edu.vn.gms.dto.request.ServiceRatingRequest;
import fpt.edu.vn.gms.dto.response.ServiceRatingResponse;
import fpt.edu.vn.gms.entity.Customer;
import fpt.edu.vn.gms.entity.ServiceRating;
import fpt.edu.vn.gms.entity.ServiceTicket;
import fpt.edu.vn.gms.exception.ResourceNotFoundException;
import fpt.edu.vn.gms.mapper.ServiceRatingMapper;
import fpt.edu.vn.gms.repository.ServiceRatingRepository;
import fpt.edu.vn.gms.repository.ServiceTicketRepository;
import fpt.edu.vn.gms.service.ServiceRatingService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.AccessLevel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ServiceRatingServiceImpl implements ServiceRatingService {

    ServiceRatingRepository serviceRatingRepository;
    ServiceTicketRepository serviceTicketRepository;
    ServiceRatingMapper serviceRatingMapper;

    @Override
    @Transactional
    public ServiceRatingResponse createRating(ServiceRatingRequest request) {
        // Tìm phiếu dịch vụ
        ServiceTicket ticket = serviceTicketRepository.findById(request.getServiceTicketId())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy phiếu dịch vụ với ID: " + request.getServiceTicketId()));

        // Lấy customer từ phiếu dịch vụ (không nhận từ FE)
        Customer customer = ticket.getCustomer();
        if (customer == null) {
            throw new ResourceNotFoundException("Phiếu dịch vụ không gắn với khách hàng, không thể tạo đánh giá");
        }

        ServiceRating rating = ServiceRating.builder()
                .serviceTicket(ticket)
                .customer(customer)
                .stars(request.getStars())
                .feedback(request.getFeedback())
                .build();

        ServiceRating saved = serviceRatingRepository.save(rating);

        return serviceRatingMapper.toDto(saved);
    }
}
