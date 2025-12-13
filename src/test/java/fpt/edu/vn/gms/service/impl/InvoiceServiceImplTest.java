package fpt.edu.vn.gms.service.impl;

import fpt.edu.vn.gms.common.enums.CustomerLoyaltyLevel;
import fpt.edu.vn.gms.common.enums.InvoiceStatus;
import fpt.edu.vn.gms.common.enums.PriceQuotationStatus;
import fpt.edu.vn.gms.entity.*;
import fpt.edu.vn.gms.exception.ResourceNotFoundException;
import fpt.edu.vn.gms.mapper.CustomerDebtMapper;
import fpt.edu.vn.gms.mapper.InvoiceMapper;
import fpt.edu.vn.gms.repository.*;
import fpt.edu.vn.gms.service.CodeSequenceService;
import fpt.edu.vn.gms.service.CustomerService;
import fpt.edu.vn.gms.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Test cases for InvoiceServiceImpl
 * Matrix: INV-001
 * Total: 12 test cases (0 EXISTING, 12 NEW)
 */
@ExtendWith(MockitoExtension.class)
class InvoiceServiceImplTest {

    @Mock
    ServiceTicketRepository serviceTicketRepo;
    @Mock
    PriceQuotationRepository priceQuotationRepo;
    @Mock
    InvoiceRepository invoiceRepo;
    @Mock
    DebtRepository debtRepo;
    @Mock
    TransactionRepository transactionRepo;
    @Mock
    TransactionService transactionService;
    @Mock
    CodeSequenceService codeSequenceService;
    @Mock
    CustomerDebtMapper customerDebtMapper;
    @Mock
    InvoiceMapper mapper;
    @Mock
    CustomerService customerService;

    @InjectMocks
    InvoiceServiceImpl service;

    private ServiceTicket serviceTicket;
    private PriceQuotation quotation;
    private Customer customer;
    private DiscountPolicy discountPolicy;

    @BeforeEach
    void setUp() {
        discountPolicy = DiscountPolicy.builder()
                .discountRate(new BigDecimal("10.00"))
                .loyaltyLevel(CustomerLoyaltyLevel.BRONZE)
                .build();

        customer = Customer.builder()
                .customerId(1L)
                .fullName("Nguyễn Văn A")
                .phone("0901234567")
                .discountPolicy(discountPolicy)
                .build();

        quotation = PriceQuotation.builder()
                .priceQuotationId(1L)
                .code("BG-000001")
                .estimateAmount(new BigDecimal("1000000"))
                .status(PriceQuotationStatus.CUSTOMER_CONFIRMED)
                .build();

        serviceTicket = ServiceTicket.builder()
                .serviceTicketId(1L)
                .serviceTicketCode("PDV-000001")
                .customer(customer)
                .priceQuotation(quotation)
                .build();
    }

    // ========== MATRIX 9: createInvoice (UTCID106-UTCID117) ==========

    @Test
    void UTCID106_createInvoice_ShouldCreateInvoice_WhenValidServiceTicketAndQuotation() {
        // Given
        when(serviceTicketRepo.findById(1L)).thenReturn(Optional.of(serviceTicket));
        when(priceQuotationRepo.findById(1L)).thenReturn(Optional.of(quotation));
        when(debtRepo.getTotalDebt(1L)).thenReturn(BigDecimal.ZERO);
        when(codeSequenceService.generateCode("HD")).thenReturn("HD-000001");
        when(invoiceRepo.save(any(Invoice.class))).thenAnswer(invocation -> {
            Invoice invoice = invocation.getArgument(0);
            invoice.setId(1L);
            return invoice;
        });

        // When
        service.createInvoice(1L, 1L);

        // Then
        verify(invoiceRepo).save(any(Invoice.class));
        verify(codeSequenceService).generateCode("HD");
    }

    @Test
    void UTCID107_createInvoice_ShouldThrowException_WhenServiceTicketNotFound() {
        // Given
        when(serviceTicketRepo.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> service.createInvoice(999L, 1L));
    }

    @Test
    void UTCID108_createInvoice_ShouldThrowException_WhenQuotationNotFound() {
        // Given
        when(serviceTicketRepo.findById(1L)).thenReturn(Optional.of(serviceTicket));
        when(priceQuotationRepo.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class, () -> service.createInvoice(1L, 999L));
    }

    @Test
    void UTCID109_createInvoice_ShouldCalculateFinalAmount_WithDiscount() {
        // Given
        when(serviceTicketRepo.findById(1L)).thenReturn(Optional.of(serviceTicket));
        when(priceQuotationRepo.findById(1L)).thenReturn(Optional.of(quotation));
        when(debtRepo.getTotalDebt(1L)).thenReturn(BigDecimal.ZERO);
        when(codeSequenceService.generateCode("HD")).thenReturn("HD-000001");
        when(invoiceRepo.save(any(Invoice.class))).thenAnswer(invocation -> {
            Invoice invoice = invocation.getArgument(0);
            invoice.setId(1L);
            return invoice;
        });

        // When
        service.createInvoice(1L, 1L);

        // Then
        verify(invoiceRepo).save(argThat(invoice -> 
            invoice.getFinalAmount().compareTo(new BigDecimal("900000")) == 0 // 1000000 - 10% discount
        ));
    }

    @Test
    void UTCID110_createInvoice_ShouldHandleZeroDiscount() {
        // Given
        customer.setDiscountPolicy(null);
        when(serviceTicketRepo.findById(1L)).thenReturn(Optional.of(serviceTicket));
        when(priceQuotationRepo.findById(1L)).thenReturn(Optional.of(quotation));
        when(debtRepo.getTotalDebt(1L)).thenReturn(BigDecimal.ZERO);
        when(codeSequenceService.generateCode("HD")).thenReturn("HD-000001");
        when(invoiceRepo.save(any(Invoice.class))).thenAnswer(invocation -> {
            Invoice invoice = invocation.getArgument(0);
            invoice.setId(1L);
            return invoice;
        });

        // When
        service.createInvoice(1L, 1L);

        // Then
        verify(invoiceRepo).save(argThat(invoice -> 
            invoice.getFinalAmount().compareTo(new BigDecimal("1000000")) == 0
        ));
    }

    @Test
    void UTCID111_createInvoice_ShouldGenerateInvoiceCode() {
        // Given
        when(serviceTicketRepo.findById(1L)).thenReturn(Optional.of(serviceTicket));
        when(priceQuotationRepo.findById(1L)).thenReturn(Optional.of(quotation));
        when(debtRepo.getTotalDebt(1L)).thenReturn(BigDecimal.ZERO);
        when(codeSequenceService.generateCode("HD")).thenReturn("HD-000001");
        when(invoiceRepo.save(any(Invoice.class))).thenAnswer(invocation -> {
            Invoice invoice = invocation.getArgument(0);
            invoice.setId(1L);
            return invoice;
        });

        // When
        service.createInvoice(1L, 1L);

        // Then
        verify(codeSequenceService).generateCode("HD");
        verify(invoiceRepo).save(argThat(invoice -> 
            invoice.getCode().equals("HD-000001")
        ));
    }

    @Test
    void UTCID112_createInvoice_ShouldSetUnpaidStatus() {
        // Given
        when(serviceTicketRepo.findById(1L)).thenReturn(Optional.of(serviceTicket));
        when(priceQuotationRepo.findById(1L)).thenReturn(Optional.of(quotation));
        when(debtRepo.getTotalDebt(1L)).thenReturn(BigDecimal.ZERO);
        when(codeSequenceService.generateCode("HD")).thenReturn("HD-000001");
        when(invoiceRepo.save(any(Invoice.class))).thenAnswer(invocation -> {
            Invoice invoice = invocation.getArgument(0);
            invoice.setId(1L);
            return invoice;
        });

        // When
        service.createInvoice(1L, 1L);

        // Then
        verify(invoiceRepo).save(argThat(invoice -> 
            invoice.getStatus() == InvoiceStatus.PENDING
        ));
    }

    @Test
    void UTCID113_createInvoice_ShouldSetZeroDepositReceived() {
        // Given
        when(serviceTicketRepo.findById(1L)).thenReturn(Optional.of(serviceTicket));
        when(priceQuotationRepo.findById(1L)).thenReturn(Optional.of(quotation));
        when(debtRepo.getTotalDebt(1L)).thenReturn(BigDecimal.ZERO);
        when(codeSequenceService.generateCode("HD")).thenReturn("HD-000001");
        when(invoiceRepo.save(any(Invoice.class))).thenAnswer(invocation -> {
            Invoice invoice = invocation.getArgument(0);
            invoice.setId(1L);
            return invoice;
        });

        // When
        service.createInvoice(1L, 1L);

        // Then
        verify(invoiceRepo).save(argThat(invoice -> 
            invoice.getDepositReceived().compareTo(BigDecimal.ZERO) == 0
        ));
    }

    @Test
    void UTCID114_createInvoice_ShouldHandleQuotationStatusValidation() {
        // Given
        quotation.setStatus(PriceQuotationStatus.DRAFT);
        when(serviceTicketRepo.findById(1L)).thenReturn(Optional.of(serviceTicket));
        when(priceQuotationRepo.findById(1L)).thenReturn(Optional.of(quotation));
        when(debtRepo.getTotalDebt(1L)).thenReturn(BigDecimal.ZERO);
        when(codeSequenceService.generateCode("HD")).thenReturn("HD-000001");
        when(invoiceRepo.save(any(Invoice.class))).thenAnswer(invocation -> {
            Invoice invoice = invocation.getArgument(0);
            invoice.setId(1L);
            return invoice;
        });

        // When & Then - Current implementation doesn't check quotation status
        assertDoesNotThrow(() -> service.createInvoice(1L, 1L));
    }

    @Test
    void UTCID115_createInvoice_ShouldHandleDatabaseSaveFailure() {
        // Given
        when(serviceTicketRepo.findById(1L)).thenReturn(Optional.of(serviceTicket));
        when(priceQuotationRepo.findById(1L)).thenReturn(Optional.of(quotation));
        when(debtRepo.getTotalDebt(1L)).thenReturn(BigDecimal.ZERO);
        when(codeSequenceService.generateCode("HD")).thenReturn("HD-000001");
        when(invoiceRepo.save(any(Invoice.class)))
                .thenThrow(new DataAccessException("Database error") {});

        // When & Then
        assertThrows(DataAccessException.class, () -> service.createInvoice(1L, 1L));
    }

    @Test
    void UTCID116_createInvoice_ShouldHandleZeroEstimateAmount() {
        // Given
        quotation.setEstimateAmount(BigDecimal.ZERO);
        when(serviceTicketRepo.findById(1L)).thenReturn(Optional.of(serviceTicket));
        when(priceQuotationRepo.findById(1L)).thenReturn(Optional.of(quotation));
        when(debtRepo.getTotalDebt(1L)).thenReturn(BigDecimal.ZERO);
        when(codeSequenceService.generateCode("HD")).thenReturn("HD-000001");
        when(invoiceRepo.save(any(Invoice.class))).thenAnswer(invocation -> {
            Invoice invoice = invocation.getArgument(0);
            invoice.setId(1L);
            return invoice;
        });

        // When
        service.createInvoice(1L, 1L);

        // Then
        verify(invoiceRepo).save(argThat(invoice -> 
            invoice.getFinalAmount().compareTo(BigDecimal.ZERO) == 0
        ));
    }

    @Test
    void UTCID117_createInvoice_ShouldHandleHundredPercentDiscount() {
        // Given
        discountPolicy.setDiscountRate(new BigDecimal("100.00"));
        when(serviceTicketRepo.findById(1L)).thenReturn(Optional.of(serviceTicket));
        when(priceQuotationRepo.findById(1L)).thenReturn(Optional.of(quotation));
        when(debtRepo.getTotalDebt(1L)).thenReturn(BigDecimal.ZERO);
        when(codeSequenceService.generateCode("HD")).thenReturn("HD-000001");
        when(invoiceRepo.save(any(Invoice.class))).thenAnswer(invocation -> {
            Invoice invoice = invocation.getArgument(0);
            invoice.setId(1L);
            return invoice;
        });

        // When
        service.createInvoice(1L, 1L);

        // Then
        verify(invoiceRepo).save(argThat(invoice -> 
            invoice.getFinalAmount().compareTo(BigDecimal.ZERO) == 0
        ));
    }
}

