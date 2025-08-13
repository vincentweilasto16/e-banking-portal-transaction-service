package com.e_banking_portal.transaction_service.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "transactions")
public class TransactionEntity implements Serializable {

 @Id
 private UUID id;

 @Column(name = "user_id", nullable = false)
 @JsonProperty("user_id")
 private String userId;

 @Column(nullable = false)
 private BigDecimal amount;

 @Column(nullable = false, length = 3)
 private String currency;

 @Column(nullable = false)
 private String iban;

 @Column(name = "value_date", nullable = false)
 private LocalDate valueDate;

 @Column(length = 255)
 private String description;

 public TransactionEntity() {
 }

 public TransactionEntity(UUID id, String userId, BigDecimal amount, String currency, String iban, LocalDate valueDate, String description) {
  this.id = id;
  this.userId = userId;
  this.amount = amount;
  this.currency = currency;
  this.iban = iban;
  this.valueDate = valueDate;
  this.description = description;
 }

 public UUID getId() {
  return id;
 }

 public void setId(UUID id) {
  this.id = id;
 }

 public String getUserId() {
  return userId;
 }

 public void setUserId(String userId) {
  this.userId = userId;
 }

 public BigDecimal getAmount() {
  return amount;
 }

 public void setAmount(BigDecimal amount) {
  this.amount = amount;
 }

 public String getCurrency() {
  return currency;
 }

 public void setCurrency(String currency) {
  this.currency = currency;
 }

 public String getIban() {
  return iban;
 }

 public void setIban(String iban) {
  this.iban = iban;
 }

 public LocalDate getValueDate() {
  return valueDate;
 }

 public void setValueDate(LocalDate valueDate) {
  this.valueDate = valueDate;
 }

 public String getDescription() {
  return description;
 }

 public void setDescription(String description) {
  this.description = description;
 }
}