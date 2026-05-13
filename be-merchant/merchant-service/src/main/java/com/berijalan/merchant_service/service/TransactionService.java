package com.berijalan.merchant_service.service;

import com.berijalan.merchant_service.dto.request.ReqTransactionDto;
import com.berijalan.merchant_service.dto.response.ResAdminStatsDto;
import com.berijalan.merchant_service.dto.response.ResMerchantStatsDto;
import com.berijalan.merchant_service.entity.MerchantTransactionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.UUID;


public interface TransactionService {
    MerchantTransactionEntity buyProduct(String userId, ReqTransactionDto request);
    MerchantTransactionEntity getTransactionById(String userId, String role, UUID transactionId);
    Page<MerchantTransactionEntity> getAllTransactions(String userId, String role, Pageable pageable, LocalDate startDate, LocalDate endDate);
    ResAdminStatsDto getAdminStats();
    ResMerchantStatsDto getMerchantStats(String userId, String role, UUID merchantId);
}
