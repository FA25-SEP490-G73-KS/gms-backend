package fpt.edu.vn.gms.service;

import fpt.edu.vn.gms.dto.PayInvoiceRequestDto;
import fpt.edu.vn.gms.dto.TransactionResponseDto;
import fpt.edu.vn.gms.dto.response.DebtDetailResponseDto;
import fpt.edu.vn.gms.dto.response.InvoiceDetailResDto;
import fpt.edu.vn.gms.dto.response.InvoiceListResDto;
import org.springframework.data.domain.Page;

import java.time.LocalDate;

public interface InvoiceService {

    Page<InvoiceListResDto> getInvoiceList(int page, int size, String sort);

    void createInvoice(Long serviceTicketId, Long quotationId);

    InvoiceDetailResDto getInvoiceDetail(Long invoiceId);

    TransactionResponseDto payInvoice(Long invoiceId, PayInvoiceRequestDto request) throws Exception;

    /**
     * Tạo công nợ mới cho khách dựa trên phiếu thanh toán & các transaction đã có.
     */
    DebtDetailResponseDto createDebtFromInvoice(Long invoiceId, LocalDate dueDate);
}
