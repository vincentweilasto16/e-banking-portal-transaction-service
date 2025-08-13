package com.e_banking_portal.transaction_service.kafka.consumer;

import com.e_banking_portal.transaction_service.entity.TransactionEntity;
import com.e_banking_portal.transaction_service.repository.TransactionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class TransactionConsumer {

 @Autowired
 private TransactionRepository transactionRepository;

 @Autowired
 private ObjectMapper objectMapper;

 @KafkaListener(topics = "transactions", groupId = "transaction-service")
 public void consume(ConsumerRecord<String, String> record) {
  try {
   String transactionJson = record.value();
   TransactionEntity transactionEntity = objectMapper.readValue(transactionJson, TransactionEntity.class);

   // Extract userId from transaction - you may need to add userId to TransactionModel or parse it here
   String userId = extractUserId(transactionEntity); // implement this method based on your data

   transactionRepository.save(transactionEntity);

   // Optionally log consumed transaction
   System.out.println("Consumed transaction: " + transactionEntity.getId() + " for user " + userId);

  } catch (Exception e) {
   e.printStackTrace();
  }
 }

 private String extractUserId(TransactionEntity transactionEntity) {
  // TODO: Implement logic to get userId from transaction data or its metadata
  // For now, just return a dummy userId for demo
  return "P-0123456789";
 }
}
