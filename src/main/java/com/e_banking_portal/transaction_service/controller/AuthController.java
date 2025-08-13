package com.e_banking_portal.transaction_service.controller;

import com.e_banking_portal.transaction_service.response.JwtResponse;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Base64;
import java.util.Date;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

 @Value("${jwt.secret}")
 private String jwtSecret;

 @Value("${jwt.expiration-ms}")
 private long jwtExpirationMs;

 @PostMapping("/generate-token")
 public ResponseEntity<?> generateToken(@RequestParam String userId) {
  SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));

  String token = Jwts.builder()
          .setSubject(String.valueOf(userId))
          .setIssuedAt(new Date())
          .setExpiration(new Date(System.currentTimeMillis() + 3600000))
          .signWith(key, SignatureAlgorithm.HS256)
          .compact();

  return ResponseEntity.ok(new JwtResponse(token));
 }
}
