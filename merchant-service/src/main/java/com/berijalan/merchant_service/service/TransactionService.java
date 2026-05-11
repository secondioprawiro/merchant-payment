package com.berijalan.merchant_service.service;

import com.berijalan.merchant_service.dto.request.ReqTransactionDto;
import com.berijalan.merchant_service.entity.MerchantTransactionEntity;
import org.springframework.stereotype.Service;


public interface TransactionService {
    MerchantTransactionEntity buyProduct(String userId, ReqTransactionDto request);
}
