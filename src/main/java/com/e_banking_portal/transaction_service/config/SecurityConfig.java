package com.e_banking_portal.transaction_service.config;

import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;

import javax.crypto.SecretKey;
import java.util.Base64;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

 @Value("${jwt.secret}")
 private String jwtSecret;

 @Bean
 public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
  http
          .authorizeHttpRequests(auth -> auth
                  .requestMatchers(
                          "/swagger-ui/**",      // Swagger UI resources
                          "/v3/api-docs/**",     // OpenAPI docs
                          "/swagger-resources/**", // swagger resources
                          "/webjars/**",          // swagger webjars
                          "/actuator/**",          // optionally actuator endpoints
                          "/api/v1/auth/generate-token" // generate jwt token api endpoint
                  ).permitAll()
                  .anyRequest().authenticated()
          )
          .csrf(csrf -> csrf.disable())  // disable if you want to test POST without CSRF token
          .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> {}));

  return http.build();
 }

 @Bean
 public JwtDecoder jwtDecoder() {
  byte[] keyBytes = Base64.getDecoder().decode(jwtSecret);
  SecretKey key = Keys.hmacShaKeyFor(keyBytes);
  return NimbusJwtDecoder.withSecretKey(key).build();
 }
}