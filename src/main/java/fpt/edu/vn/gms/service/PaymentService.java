package fpt.edu.vn.gms.service;

import fpt.edu.vn.gms.dto.response.PaymentDetailResDto;
import fpt.edu.vn.gms.dto.response.PaymentListResDto;
import org.springframework.data.domain.Page;

public interface PaymentService {

    Page<PaymentListResDto> getPaymentList(int page, int size, String sort);

    void createPayment(Long serviceTicketId, Long quotationId);

    PaymentDetailResDto getPaymentDetail(Long paymentId);
}
