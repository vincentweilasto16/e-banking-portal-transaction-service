package com.e_banking_portal.transaction_service.service;

import com.e_banking_portal.transaction_service.entity.TransactionEntity;
import com.e_banking_portal.transaction_service.repository.TransactionRepository;
import com.e_banking_portal.transaction_service.response.PaginationResponse;
import com.e_banking_portal.transaction_service.response.TransactionResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class TransactionServiceImpl implements TransactionService {

 @Autowired
 private TransactionRepository transactionRepository;

 @Autowired
 private ExchangeRateService exchangeRateService;


 @Override
 public PaginationResponse<TransactionEntity> getTransactionsByUserID(String userId, int year, int month, int page, int size) {
  Pageable pageable = PageRequest.of(page, size);

  Page<TransactionEntity> pageResult = transactionRepository.findByUserIdAndYearAndMonth(userId, year, month, pageable);

  BigDecimal totalCredit = BigDecimal.ZERO;
  BigDecimal totalDebit = BigDecimal.ZERO;

//  for (TransactionEntity tx : pageResult.getContent()) {
//   // Exchange rate for transaction date
//   BigDecimal rate = exchangeRateService.getRate(tx.getCurrency(), "USD", tx.getValueDate());
//   BigDecimal amountInBase = tx.getAmount().multiply(rate);
//
//   if (amountInBase.compareTo(BigDecimal.ZERO) > 0) {
//    totalCredit = totalCredit.add(amountInBase);
//   } else {
//    totalDebit = totalDebit.add(amountInBase.abs());
//   }
//  }

  TransactionResponse data = new TransactionResponse(
          pageResult.getContent(),
          totalCredit,
          totalDebit
  );

  return new PaginationResponse<>(
          data,
          page,
          size,
          pageResult.getTotalElements()
  );
 }
}

