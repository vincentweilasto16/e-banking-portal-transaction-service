package com.e_banking_portal.transaction_service.service;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface ExchangeRateService {
 BigDecimal getRate(String sourceCurrency, String targetCurrency, LocalDate date);
}
