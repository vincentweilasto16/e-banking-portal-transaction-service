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

   transactionRepository.save(transactionEntity);

   // Optionally log consumed transaction
   System.out.println("Consumed transaction: " + transactionEntity.getId() + " for user " + transactionEntity.getUserId());

  } catch (Exception e) {
   e.printStackTrace();
  }
 }
}
