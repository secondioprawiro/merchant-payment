package com.berijalan.merchant_service.service;

import com.berijalan.merchant_service.dto.request.ReqTransactionDto;
import com.berijalan.merchant_service.dto.response.ResAdminStatsDto;
import com.berijalan.merchant_service.dto.response.ResMerchantStatsDto;
import com.berijalan.merchant_service.entity.MerchantTransactionEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


public interface TransactionService {
    MerchantTransactionEntity buyProduct(String userId, ReqTransactionDto request);
    MerchantTransactionEntity getTransactionById(String userId, String role, UUID transactionId);
    List<MerchantTransactionEntity> getAllTransactions(String userId, String role);
    ResAdminStatsDto getAdminStats();
    ResMerchantStatsDto getMerchantStats(String userId, String role, UUID merchantId);
}
