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
import com.berijalan.merchant_service.repository.MerchantRepository;
import com.berijalan.merchant_service.repository.MerchantTransactionRepository;
import com.berijalan.merchant_service.service.TransactionService;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
                .orElseThrow(() -> new DataNotFoundException("Merchant tidak ditemukan"));
        if (!merchant.getStatus().equals(MerchantEntity.Status.ACTIVE)) {
            throw new BadRequestException("Merchant tidak aktif");
        }

        BaseResponse<ResProductDto> productData;
        try {
            productData = productClient.getProductById(request.getProductId());
        } catch (FeignException.NotFound e) {
            throw new DataNotFoundException("Produk tidak ditemukan");
        } catch (FeignException e) {
            throw new BadRequestException("Gagal menghubungi product service");
        }

        if (!productData.getData().getStatus().equals("AVAILABLE")) {
            throw new BadRequestException("Produk tidak tersedia");
        }

        // 3. Validasi nomor tujuan
        String type = productData.getData().getType();
        if (type.equals("PULSA")) {
            if (!request.getNomorTujuan().matches("^[0-9]{10,13}$")) {
                throw new BadRequestException("Nomor HP tidak valid");
            }
        } else if (type.equals("PLN")) {
            if (!request.getNomorTujuan().matches("^[0-9]{11,12}$")) {
                throw new BadRequestException("Nomor meter PLN tidak valid");
            }
        }
        ResTransactionDto productResponse = productClient.processTransaction(request).getData();

        System.out.println("Full response: " + productResponse);
        System.out.println("Data: " + productResponse.getStatus());


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

        return transactionRepository.save(transaction);
    }

    @Override
    public MerchantTransactionEntity getTransactionById(String userId, String role, UUID transactionId) {
        MerchantTransactionEntity transaction = transactionRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new DataNotFoundException("Transaksi tidak ditemukan"));

        if (!role.equals("ADMIN")){
            MerchantEntity merchant = merchantRepository.findByAccountId(userId)
                    .orElseThrow(() -> new DataNotFoundException("Merchant tidak ditemukan"));
            if (!transaction.getMerchantId().toString().equals((merchant.getMerchantId().toString()))){
                throw new BadRequestException("Tidak memiliki akses ke transaksi ini");
            }
        }
        return transaction;
    }
}
