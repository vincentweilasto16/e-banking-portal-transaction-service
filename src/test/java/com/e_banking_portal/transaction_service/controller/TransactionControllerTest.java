package com.e_banking_portal.transaction_service.controller;

import com.e_banking_portal.transaction_service.controller.TransactionController;
import com.e_banking_portal.transaction_service.entity.TransactionEntity;
import com.e_banking_portal.transaction_service.response.PaginationResponse;
import com.e_banking_portal.transaction_service.response.TransactionResponse;
import com.e_banking_portal.transaction_service.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest
class TransactionControllerTest {

    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private TransactionController transactionController;

    @Mock
    private Jwt jwt;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetTransactionsByUserID() {
        // Mock JWT subject
        when(jwt.getSubject()).thenReturn("user-123");

        // Prepare mock transaction
        TransactionEntity transaction = new TransactionEntity(
                UUID.randomUUID(),
                "user-123",
                BigDecimal.valueOf(100),
                "USD",
                "IBAN123",
                LocalDate.now(),
                "Test transaction"
        );

        TransactionResponse transactionResponse = new TransactionResponse(
                Collections.singletonList(transaction),
                BigDecimal.valueOf(100),
                BigDecimal.ZERO
        );

        PaginationResponse<TransactionEntity> mockPagination = new PaginationResponse<>(
                new TransactionResponse(Collections.singletonList(transaction), BigDecimal.valueOf(100), BigDecimal.ZERO),
                0,  // page number
                10, // page size
                1L  // total elements
        );

        // Mock the service
        when(transactionService.getTransactionsByUserID("user-123", 2025, 8, 0, 10))
                .thenReturn(mockPagination);

        // Call controller
        ResponseEntity<PaginationResponse<TransactionEntity>> response = transactionController
                .getTransactionsByUserID(jwt, 2025, 8, 1, 10);

        // Verify
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(mockPagination.getTotalElements(), response.getBody().getTotalElements());
        verify(transactionService, times(1))
                .getTransactionsByUserID("user-123", 2025, 8, 0, 10);
    }

    @Test
    void testGetTransactionsInvalidPage() {
        when(jwt.getSubject()).thenReturn("user-123");

        PaginationResponse<TransactionEntity> mockPagination = new PaginationResponse<>(
                new TransactionResponse(Collections.emptyList(), BigDecimal.ZERO, BigDecimal.ZERO),
                0, 10, 0L
        );

        when(transactionService.getTransactionsByUserID("user-123", 2025, 8, 0, 10))
                .thenReturn(mockPagination);

        ResponseEntity<PaginationResponse<TransactionEntity>> response = transactionController
                .getTransactionsByUserID(jwt, 2025, 8, -1, 10); // invalid page

        assertEquals(200, response.getStatusCodeValue());
        assertEquals(0, response.getBody().getTotalElements());
    }

}