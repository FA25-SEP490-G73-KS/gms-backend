package fpt.edu.vn.gms.service.impl;

import fpt.edu.vn.gms.common.enums.CustomerLoyaltyLevel;
import fpt.edu.vn.gms.entity.Customer;
import fpt.edu.vn.gms.entity.DiscountPolicy;
import fpt.edu.vn.gms.exception.ResourceNotFoundException;
import fpt.edu.vn.gms.repository.CustomerRepository;
import fpt.edu.vn.gms.repository.DiscountPolicyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataAccessException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Test cases for CustomerServiceImpl
 * Based on TEST_CASE_DESIGN_DOCUMENT.md
 * Matrix: CUST-001
 * Total: 10 test cases (0 EXISTING, 10 NEW)
 */
@ExtendWith(MockitoExtension.class)
class CustomerServiceImplTest {

    @Mock
    CustomerRepository customerRepository;
    @Mock
    DiscountPolicyRepository discountPolicyRepository;

    @InjectMocks
    CustomerServiceImpl service;

    private Customer customer;
    private DiscountPolicy bronzePolicy;
    private DiscountPolicy silverPolicy;
    private DiscountPolicy goldPolicy;

    @BeforeEach
    void setUp() {
        bronzePolicy = DiscountPolicy.builder()
                .discountPolicyId(1L)
                .loyaltyLevel(CustomerLoyaltyLevel.BRONZE)
                .discountRate(new BigDecimal("5.00"))
                .requiredSpending(new BigDecimal("0"))
                .build();

        silverPolicy = DiscountPolicy.builder()
                .discountPolicyId(2L)
                .loyaltyLevel(CustomerLoyaltyLevel.SLIVER)
                .discountRate(new BigDecimal("10.00"))
                .requiredSpending(new BigDecimal("1000000"))
                .build();

        goldPolicy = DiscountPolicy.builder()
                .discountPolicyId(3L)
                .loyaltyLevel(CustomerLoyaltyLevel.GOLD)
                .discountRate(new BigDecimal("15.00"))
                .requiredSpending(new BigDecimal("5000000"))
                .build();

        customer = Customer.builder()
                .customerId(1L)
                .fullName("Nguyễn Văn A")
                .phone("0901234567")
                .totalSpending(BigDecimal.ZERO)
                .discountPolicy(bronzePolicy)
                .build();
    }

    // ========== MATRIX 10: updateTotalSpending (CUST-001) - UTCID118-UTCID127 ==========

    /**
     * UTCID118: Valid customer, positive amount
     * Precondition: Valid customer, positive amount
     * Input: customerId=1L, amount=100000
     * Expected: Updates totalSpending, may update loyalty level
     * Type: N (Normal)
     */
    @Test
    void updateTotalSpending_UTCID118_ShouldUpdateTotalSpending_WhenValidCustomerAndPositiveAmount() {
        // Given
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(discountPolicyRepository.findAll()).thenReturn(List.of(bronzePolicy, silverPolicy, goldPolicy));
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        // When
        service.updateTotalSpending(1L, new BigDecimal("100000"));

        // Then
        verify(customerRepository).save(argThat(c ->
            c.getTotalSpending().compareTo(new BigDecimal("100000")) == 0
        ));
    }

    /**
     * UTCID119: Customer not found
     * Precondition: Customer not found
     * Input: customerId=999L
     * Expected: Throws ResourceNotFoundException
     * Type: A (Abnormal)
     */
    @Test
    void updateTotalSpending_UTCID119_ShouldThrowException_WhenCustomerNotFound() {
        // Given
        when(customerRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(ResourceNotFoundException.class,
                () -> service.updateTotalSpending(999L, new BigDecimal("100000")));
    }

    /**
     * UTCID120: Amount = 0
     * Precondition: Amount = 0
     * Input: amount=0
     * Expected: Either no update or updates to same value
     * Type: B (Boundary)
     */
    @Test
    void updateTotalSpending_UTCID120_ShouldHandle_WhenAmountIsZero() {
        // Given
        customer.setTotalSpending(new BigDecimal("100000"));
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(discountPolicyRepository.findAll()).thenReturn(List.of(bronzePolicy, silverPolicy, goldPolicy));
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        // When
        service.updateTotalSpending(1L, BigDecimal.ZERO);

        // Then
        verify(customerRepository).save(argThat(c ->
            c.getTotalSpending().compareTo(new BigDecimal("100000")) == 0
        ));
    }

    /**
     * UTCID121: Amount < 0
     * Precondition: Amount < 0
     * Input: amount=-100000
     * Expected: Either throws exception or allows negative
     * Type: A (Abnormal)
     */
    @Test
    void updateTotalSpending_UTCID121_ShouldHandle_WhenAmountIsNegative() {
        // Given
        customer.setTotalSpending(new BigDecimal("200000"));
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(discountPolicyRepository.findAll()).thenReturn(List.of(bronzePolicy, silverPolicy, goldPolicy));
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        // When & Then
        // Note: Current implementation allows negative amounts
        assertDoesNotThrow(() -> service.updateTotalSpending(1L, new BigDecimal("-100000")));
        verify(customerRepository).save(argThat(c ->
            c.getTotalSpending().compareTo(new BigDecimal("100000")) == 0
        ));
    }

    /**
     * UTCID122: Loyalty level upgrade
     * Precondition: Loyalty level upgrade
     * Input: totalSpending crosses threshold
     * Expected: Updates loyalty level and discount policy
     * Type: N (Normal)
     */
    @Test
    void updateTotalSpending_UTCID122_ShouldUpgradeLoyaltyLevel_WhenTotalSpendingCrossesThreshold() {
        // Given
        customer.setTotalSpending(new BigDecimal("900000"));
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(discountPolicyRepository.findAll()).thenReturn(List.of(bronzePolicy, silverPolicy, goldPolicy));
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        // When
        service.updateTotalSpending(1L, new BigDecimal("200000")); // Total becomes 1100000, crosses 1000000 threshold

        // Then
        verify(customerRepository).save(argThat(c ->
            c.getTotalSpending().compareTo(new BigDecimal("1100000")) == 0 &&
            c.getDiscountPolicy().equals(silverPolicy)
        ));
    }

    /**
     * UTCID123: Loyalty level downgrade
     * Precondition: Loyalty level downgrade
     * Input: totalSpending decreases
     * Expected: Either maintains or downgrades level
     * Type: A (Abnormal)
     */
    @Test
    void updateTotalSpending_UTCID123_ShouldHandle_WhenTotalSpendingDecreases() {
        // Given
        customer.setTotalSpending(new BigDecimal("2000000"));
        customer.setDiscountPolicy(silverPolicy);
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(discountPolicyRepository.findAll()).thenReturn(List.of(bronzePolicy, silverPolicy, goldPolicy));
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        // When
        service.updateTotalSpending(1L, new BigDecimal("-1500000")); // Total becomes 500000

        // Then
        // Note: Current implementation finds best policy based on current total, so it may downgrade
        verify(customerRepository).save(argThat(c ->
            c.getTotalSpending().compareTo(new BigDecimal("500000")) == 0
        ));
    }

    /**
     * UTCID124: Concurrent updates
     * Precondition: Concurrent updates
     * Input: Multiple updates same customer
     * Expected: Last update wins or uses locking
     * Type: A (Abnormal)
     */
    @Test
    void updateTotalSpending_UTCID124_ShouldHandle_WhenConcurrentUpdates() {
        // Given
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(discountPolicyRepository.findAll()).thenReturn(List.of(bronzePolicy, silverPolicy, goldPolicy));
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        // When - Simulate concurrent updates
        service.updateTotalSpending(1L, new BigDecimal("100000"));
        service.updateTotalSpending(1L, new BigDecimal("200000"));

        // Then - Last update should win (or use locking)
        verify(customerRepository, atLeastOnce()).save(any(Customer.class));
    }

    /**
     * UTCID125: Database save fails
     * Precondition: Database save fails
     * Input: repository.save() throws exception
     * Expected: Throws DataAccessException
     * Type: A (Abnormal)
     */
    @Test
    void updateTotalSpending_UTCID125_ShouldThrowException_WhenDatabaseSaveFails() {
        // Given
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(discountPolicyRepository.findAll()).thenReturn(List.of(bronzePolicy, silverPolicy, goldPolicy));
        when(customerRepository.save(any(Customer.class)))
                .thenThrow(new DataAccessException("Database error") {});

        // When & Then
        assertThrows(DataAccessException.class,
                () -> service.updateTotalSpending(1L, new BigDecimal("100000")));
    }

    /**
     * UTCID126: Boundary: amount = BigDecimal.MAX_VALUE
     * Precondition: Boundary: amount = BigDecimal.MAX_VALUE
     * Input: amount=MAX_VALUE
     * Expected: Either succeeds or throws exception
     * Type: B (Boundary)
     */
    @Test
    void updateTotalSpending_UTCID126_ShouldHandle_WhenAmountIsMaxValue() {
        // Given
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(discountPolicyRepository.findAll()).thenReturn(List.of(bronzePolicy, silverPolicy, goldPolicy));
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        // When & Then
        assertDoesNotThrow(() -> service.updateTotalSpending(1L, BigDecimal.valueOf(Long.MAX_VALUE)));
    }

    /**
     * UTCID127: Boundary: totalSpending overflow
     * Precondition: Boundary: totalSpending overflow
     * Input: Very large total
     * Expected: Either succeeds or throws exception
     * Type: B (Boundary)
     */
    @Test
    void updateTotalSpending_UTCID127_ShouldHandle_WhenTotalSpendingOverflow() {
        // Given
        customer.setTotalSpending(BigDecimal.valueOf(Long.MAX_VALUE));
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(discountPolicyRepository.findAll()).thenReturn(List.of(bronzePolicy, silverPolicy, goldPolicy));
        when(customerRepository.save(any(Customer.class))).thenReturn(customer);

        // When & Then
        assertDoesNotThrow(() -> service.updateTotalSpending(1L, new BigDecimal("100000")));
    }

    // ========== MATRIX 11: PartServiceImpl.updateInventory (PART-001) - UTCID128-UTCID139 ==========
    // Note: Method updateInventory does not exist in PartService interface
    // This may be implemented in a different service or needs to be created
    // For now, we'll create a placeholder test class structure

}

