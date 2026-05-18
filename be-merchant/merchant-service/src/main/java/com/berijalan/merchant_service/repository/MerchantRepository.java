package com.berijalan.merchant_service.repository;

import com.berijalan.merchant_service.entity.MerchantEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MerchantRepository extends JpaRepository<MerchantEntity, UUID>, JpaSpecificationExecutor<MerchantEntity> {
    boolean existsByKodeMerchant(String kodeMerchant);
    List<MerchantEntity> findAllByIsDeletedFalse();
    Optional<MerchantEntity> findByMerchantIdAndIsDeletedFalse(UUID merchantId);
    Optional<MerchantEntity> findByAccountId(String userId);
    long countByStatus(MerchantEntity.Status status);
    long countByStatusAndIsDeletedFalse(MerchantEntity.Status status);
}
