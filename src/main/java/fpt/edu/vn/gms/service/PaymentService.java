package fpt.edu.vn.gms.service;

import fpt.edu.vn.gms.dto.response.DebtResDto;
import fpt.edu.vn.gms.dto.response.PaymentDetailResDto;
import fpt.edu.vn.gms.dto.response.PaymentListResDto;
import org.springframework.data.domain.Page;

import java.time.LocalDate;

public interface PaymentService {

    Page<PaymentListResDto> getPaymentList(int page, int size, String sort);

    void createPayment(Long serviceTicketId, Long quotationId);

    PaymentDetailResDto getPaymentDetail(Long paymentId);

    /**
     * Tạo công nợ mới cho khách dựa trên phiếu thanh toán & các transaction đã có.
     */
    DebtResDto createDebtFromPayment(Long paymentId, LocalDate dueDate);
}
