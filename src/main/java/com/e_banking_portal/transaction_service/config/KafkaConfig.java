package com.e_banking_portal.transaction_service.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;

import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;

import org.springframework.kafka.core.KafkaTemplate;

import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Configuration
public class KafkaConfig {

 @Value("${spring.kafka.bootstrap-servers}")
 private String bootstrapServers;

 @Value("${spring.kafka.consumer.group-id}")
 private String consumerGroupId;

 // Consumer Factory
 @Bean
 public ConsumerFactory<String, String> consumerFactory() {
  Map<String, Object> props = new HashMap<>();
  props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
  props.put(ConsumerConfig.GROUP_ID_CONFIG, consumerGroupId);
  props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
  props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
  props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
  return new DefaultKafkaConsumerFactory<>(props);
 }

 // Kafka Listener Container Factory
 @Bean
 public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() {
  var factory = new ConcurrentKafkaListenerContainerFactory<String, String>();
  factory.setConsumerFactory(consumerFactory());
  return factory;
 }

 // Producer Factory (optional)
 @Bean
 public ProducerFactory<String, String> producerFactory() {
  Map<String, Object> props = new HashMap<>();
  props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
  props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
  props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
  return new DefaultKafkaProducerFactory<>(props);
 }

 // Kafka Template (optional)
 @Bean
 public KafkaTemplate<String, String> kafkaTemplate() {
  return new KafkaTemplate<>(producerFactory());
 }
}
