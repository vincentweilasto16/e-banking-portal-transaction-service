package com.e_banking_portal.transaction_service.repository;

import com.e_banking_portal.transaction_service.entity.TransactionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<TransactionEntity, UUID> {
    @Query(value = "SELECT * FROM transactions WHERE user_id = :userId AND EXTRACT(YEAR FROM value_date) = :year AND EXTRACT(MONTH FROM value_date) = :month",
            nativeQuery = true)
    Page<TransactionEntity> findByUserIdAndYearAndMonth(
            @Param("userId") String userId,
            @Param("year") int year,
            @Param("month") int month,
            Pageable pageable);

}