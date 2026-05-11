package com.berijalan.merchant_service.repository;

import com.berijalan.merchant_service.entity.MerchantEntity;
import com.berijalan.merchant_service.entity.MerchantTransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MerchantTransactionRepository extends JpaRepository<MerchantTransactionEntity, UUID> {
    List<MerchantTransactionEntity> findByMerchantId(UUID merchantId);
    Optional<MerchantTransactionEntity> findByTransactionId(UUID transactionId);
}
