package com.e_banking_portal.transaction_service.response;

import com.e_banking_portal.transaction_service.entity.TransactionEntity;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.List;

public class TransactionResponse {

 @JsonProperty("transactions")
 private List<TransactionEntity> transactions;
 @JsonProperty("total_credit")
 private BigDecimal totalCredit;

 @JsonProperty("total_debit")
 private BigDecimal totalDebit;

 public TransactionResponse(List<TransactionEntity> transactions, BigDecimal totalCredit, BigDecimal totalDebit) {
  this.transactions = transactions;
  this.totalCredit = totalCredit;
  this.totalDebit = totalDebit;
 }

 public List<TransactionEntity> getTransactions() {
  return transactions;
 }

 public void setTransactions(List<TransactionEntity> transactions) {
  this.transactions = transactions;
 }

 public BigDecimal getTotalCredit() {
  return totalCredit;
 }

 public void setTotalCredit(BigDecimal totalCredit) {
  this.totalCredit = totalCredit;
 }

 public BigDecimal getTotalDebit() {
  return totalDebit;
 }

 public void setTotalDebit(BigDecimal totalDebit) {
  this.totalDebit = totalDebit;
 }
}