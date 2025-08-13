package com.e_banking_portal.transaction_service.kafka.consumer;

import com.e_banking_portal.transaction_service.entity.TransactionEntity;
import com.e_banking_portal.transaction_service.repository.TransactionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.mockito.Mockito.*;

class TransactionConsumerTest {

 @Mock
 private TransactionRepository transactionRepository;

 @Mock
 private ObjectMapper objectMapper;

 @InjectMocks
 private TransactionConsumer transactionConsumer;

 @BeforeEach
 void setUp() {
  MockitoAnnotations.openMocks(this);
 }

 @Test
 void consume_validTransaction_savesToRepository() throws Exception {
  // Prepare mock transaction JSON
  TransactionEntity transaction = new TransactionEntity(
          UUID.randomUUID(),
          "user-123",
          BigDecimal.valueOf(100),
          "USD",
          "IBAN123",
          LocalDate.now(),
          "Test transaction"
  );

  String transactionJson = "{\"id\":\"" + transaction.getId() + "\",\"userId\":\"user-123\"}"; // simplified

  // Mock ObjectMapper
  when(objectMapper.readValue(transactionJson, TransactionEntity.class))
          .thenReturn(transaction);

  // Create Kafka ConsumerRecord
  ConsumerRecord<String, String> record = new ConsumerRecord<>("transactions", 0, 0L, "key", transactionJson);

  // Call consume
  transactionConsumer.consume(record);

  // Verify repository save is called
  verify(transactionRepository, times(1)).save(transaction);
 }

 @Test
 void consume_invalidJson_printsStackTrace() throws Exception {
  String invalidJson = "invalid json";
  ConsumerRecord<String, String> record = new ConsumerRecord<>("transactions", 0, 0L, "key", invalidJson);

  // Mock ObjectMapper to throw exception
  when(objectMapper.readValue(invalidJson, TransactionEntity.class))
          .thenThrow(new RuntimeException("JSON parse error"));

  // Call consume
  transactionConsumer.consume(record);

  // Verify repository save is NOT called
  verify(transactionRepository, never()).save(any());
 }
}
