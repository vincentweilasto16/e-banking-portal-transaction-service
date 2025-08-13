package  com.e_banking_portal.transaction_service.controller;

import com.e_banking_portal.transaction_service.entity.TransactionEntity;
import com.e_banking_portal.transaction_service.response.PaginationResponse;
import com.e_banking_portal.transaction_service.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/transactions")
public class TransactionController {

 @Autowired
 private TransactionService transactionService;

 @GetMapping
 public ResponseEntity<PaginationResponse<TransactionEntity>> getTransactionsByUserID(
         @AuthenticationPrincipal Jwt jwt,
         @RequestParam int year,
         @RequestParam int month,
         @RequestParam(defaultValue = "1") int page,
         @RequestParam(defaultValue = "10") int size) {

  if (page < 1) {
   page = 1;  // Optional: validate to avoid zero or negative
  }

  // Convert 1-based page to 0-based for Spring Pageable
  int zeroBasedPage = page - 1;

  // Extract user id from JWT subject claim
  String userId = jwt.getSubject();

  PaginationResponse<TransactionEntity> result = transactionService.getTransactionsByUserID(userId, year, month, zeroBasedPage, size);
  return ResponseEntity.ok(result);
 }
}

