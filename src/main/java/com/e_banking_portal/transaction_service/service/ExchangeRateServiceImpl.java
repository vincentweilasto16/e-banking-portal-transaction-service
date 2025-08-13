package com.e_banking_portal.transaction_service.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ExchangeRateServiceImpl implements ExchangeRateService {

 private static final String API_URL = "https://api.exchangerate.host/latest?base={base}";

 private final RestTemplate restTemplate;
 // Simple cache to avoid calling API for same base currency repeatedly
 private final Map<String, Map<String, BigDecimal>> ratesCache = new ConcurrentHashMap<>();

 public ExchangeRateServiceImpl() {
  this.restTemplate = new RestTemplate();
 }

 @Override
 public BigDecimal getRate(String sourceCurrency, String targetCurrency, LocalDate date) {
  if (sourceCurrency.equalsIgnoreCase(targetCurrency)) {
   return BigDecimal.ONE;
  }

  String cacheKey = sourceCurrency.toUpperCase() + "_" + date.toString();
  Map<String, BigDecimal> rates = ratesCache.get(cacheKey);

  if (rates == null) {
   ExchangeRateResponse response = restTemplate.getForObject(API_URL, ExchangeRateResponse.class, date.toString(), sourceCurrency);
   if (response == null || response.getRates() == null) {
    throw new RuntimeException("Failed to fetch exchange rates");
   }
   rates = response.getRates();
   ratesCache.put(cacheKey, rates);
  }

  BigDecimal rate = rates.get(targetCurrency.toUpperCase());
  if (rate == null) {
   throw new RuntimeException("Exchange rate not available for " + sourceCurrency + " to " + targetCurrency);
  }

  return rate;
 }

 // DTO for parsing API response
 public static class ExchangeRateResponse {
  private String base;
  private Map<String, BigDecimal> rates;

  public String getBase() { return base; }
  public void setBase(String base) { this.base = base; }

  public Map<String, BigDecimal> getRates() { return rates; }
  public void setRates(Map<String, BigDecimal> rates) { this.rates = rates; }
 }
}
