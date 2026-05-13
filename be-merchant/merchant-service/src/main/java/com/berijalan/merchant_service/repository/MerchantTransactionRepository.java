package com.berijalan.merchant_service.repository;

import com.berijalan.merchant_service.entity.MerchantTransactionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface MerchantTransactionRepository extends JpaRepository<MerchantTransactionEntity, UUID> {
    Page<MerchantTransactionEntity> findAll(Pageable pageable);
    Page<MerchantTransactionEntity> findByMerchantId(UUID merchantId, Pageable pageable);
    Optional<MerchantTransactionEntity> findByTransactionId(UUID transactionId);
    long countByTransactionDateBetween(LocalDateTime start, LocalDateTime end);
    long countByStatusAndTransactionDateBetween(MerchantTransactionEntity.Status status, LocalDateTime start, LocalDateTime end);
    long countByStatus(MerchantTransactionEntity.Status status);
    long countByMerchantId(UUID merchantId);
    long countByMerchantIdAndStatus(UUID merchantId, MerchantTransactionEntity.Status status);
    long countByMerchantIdAndTransactionDateBetween(UUID merchantId, LocalDateTime start, LocalDateTime end);
    long countByMerchantIdAndStatusAndTransactionDateBetween(UUID merchantId, MerchantTransactionEntity.Status status, LocalDateTime start, LocalDateTime end);
}
