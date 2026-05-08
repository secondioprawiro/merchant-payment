package com.berijalan.merchant_service.repository;

import com.berijalan.merchant_service.entity.MerchantEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface MerchantRepository extends JpaRepository<MerchantEntity, UUID> {
    boolean existsByKodeMerchant(String kodeMerchant);
}
