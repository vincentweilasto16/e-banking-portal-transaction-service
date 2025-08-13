package com.e_banking_portal.transaction_service.kafka.producer;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class TransactionProducer {

 private final KafkaTemplate<String, String> kafkaTemplate;

 public TransactionProducer(KafkaTemplate<String, String> kafkaTemplate) {
  this.kafkaTemplate = kafkaTemplate;
 }

 public void sendTransactionMessage(String topic, String key, String message) {
  kafkaTemplate.send(topic, key, message);
 }
}