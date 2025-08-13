package com.e_banking_portal.transaction_service.service;

import com.e_banking_portal.transaction_service.entity.TransactionEntity;
import com.e_banking_portal.transaction_service.response.PaginationResponse;

public interface TransactionService {
    PaginationResponse<TransactionEntity> getTransactionsByUserID(String userId, int year, int month, int page, int size);
}