package com.berijalan.merchant_service.service.impl;

import com.berijalan.merchant_service.client.ProductFeignClient;
import com.berijalan.merchant_service.dto.request.ReqTransactionDto;
import com.berijalan.merchant_service.dto.response.BaseResponse;
import com.berijalan.merchant_service.dto.response.ResProductDto;
import com.berijalan.merchant_service.dto.response.ResTransactionDto;
import com.berijalan.merchant_service.entity.MerchantEntity;
import com.berijalan.merchant_service.entity.MerchantTransactionEntity;
import com.berijalan.merchant_service.exception.BadRequestException;
import com.berijalan.merchant_service.exception.DataNotFoundException;
import com.berijalan.merchant_service.exception.ForbiddenException;
import com.berijalan.merchant_service.repository.MerchantRepository;
import com.berijalan.merchant_service.repository.MerchantTransactionRepository;
import com.berijalan.merchant_service.service.TransactionService;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {
    private final MerchantRepository merchantRepository;
    private final MerchantTransactionRepository transactionRepository;
    private final ProductFeignClient productClient;

    @Override
    public MerchantTransactionEntity buyProduct(String userId, ReqTransactionDto request) {
        // 1. Validasi merchant
        MerchantEntity merchant = merchantRepository.findByAccountId(userId)
                .orElseThrow(() -> {
                    log.warn("Transaction rejected - merchant not found for userId={}", userId);
                    return new DataNotFoundException("Merchant tidak ditemukan");
                });
        if (!merchant.getStatus().equals(MerchantEntity.Status.ACTIVE)) {
            log.warn("Transaction rejected - merchant inactive: merchantId={}", merchant.getMerchantId());
            throw new BadRequestException("Merchant tidak aktif");
        }

        BaseResponse<ResProductDto> productData;
        try {
            productData = productClient.getProductById(request.getProductId());
        } catch (FeignException.NotFound e) {
            log.warn("Transaction rejected - product not found: productId={}", request.getProductId());
            throw new DataNotFoundException("Produk tidak ditemukan");
        } catch (FeignException e) {
            log.error("Failed to call product-service for productId={}: {}", request.getProductId(), e.getMessage());
            throw new BadRequestException("Gagal menghubungi product service");
        }

        if (!productData.getData().getStatus().equals("AVAILABLE")) {
            log.warn("Transaction rejected - product not available: productId={}", request.getProductId());
            throw new BadRequestException("Produk tidak tersedia");
        }

        // 3. Validasi nomor tujuan
        String type = productData.getData().getType();
        if (type.equals("PULSA")) {
            if (!request.getNomorTujuan().matches("^[0-9]{10,13}$")) {
                log.warn("Transaction rejected - invalid nomorTujuan format: type=PULSA, nomorTujuan={}", request.getNomorTujuan());
                throw new BadRequestException("Nomor HP tidak valid");
            }
        } else if (type.equals("PLN")) {
            if (!request.getNomorTujuan().matches("^[0-9]{11,12}$")) {
                log.warn("Transaction rejected - invalid nomorTujuan format: type=PLN, nomorTujuan={}", request.getNomorTujuan());
                throw new BadRequestException("Nomor meter PLN tidak valid");
            }
        }
        ResTransactionDto productResponse = productClient.processTransaction(request).getData();

        MerchantTransactionEntity transaction = new MerchantTransactionEntity();
        transaction.setMerchantId(merchant.getMerchantId());
        transaction.setProductId(request.getProductId());
        transaction.setProductName(productResponse.getProductName());
        transaction.setNomorTujuan(request.getNomorTujuan());
        transaction.setRefId(productResponse.getRefId());
        transaction.setStatus(MerchantTransactionEntity.Status.valueOf(productResponse.getStatus().toUpperCase()));
        transaction.setAmount(productResponse.getPrice());
        transaction.setFailureReason(productResponse.getFailureReason());
        transaction.setTransactionDate(LocalDateTime.now());

        MerchantTransactionEntity saved = transactionRepository.save(transaction);
        log.info("Transaction saved: transactionId={}, refId={}, status={}", saved.getTransactionId(), saved.getRefId(), saved.getStatus());
        return saved;
    }

    @Override
    public List<MerchantTransactionEntity> getAllTransactions(String userId, String role) {
        if (role.equals("ADMIN")) {
            return transactionRepository.findAll();
        }else{
            MerchantEntity merchant = merchantRepository.findByAccountId(userId)
                    .orElseThrow(() -> new DataNotFoundException("Merchant tidak ditemukan"));
            return transactionRepository.findByMerchantId(merchant.getMerchantId());
        }
    }

    @Override
    public MerchantTransactionEntity getTransactionById(String userId, String role, UUID transactionId) {
        MerchantTransactionEntity transaction = transactionRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new DataNotFoundException("Transaksi tidak ditemukan"));

        if (!role.equals("ADMIN")){
            MerchantEntity merchant = merchantRepository.findByAccountId(userId)
                    .orElseThrow(() -> new DataNotFoundException("Merchant tidak ditemukan"));
            if (!transaction.getMerchantId().toString().equals((merchant.getMerchantId().toString()))){
                log.warn("Access denied - userId={} tried to access transactionId={}", userId, transactionId);
                throw new ForbiddenException("Tidak memiliki akses ke transaksi ini");
            }
        }
        return transaction;
    }
}
