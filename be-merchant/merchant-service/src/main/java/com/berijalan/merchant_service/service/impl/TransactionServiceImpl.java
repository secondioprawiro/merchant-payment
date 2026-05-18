package com.berijalan.merchant_service.service.impl;

import com.berijalan.merchant_service.client.ProductFeignClient;
import com.berijalan.merchant_service.dto.request.ReqTransactionDto;
import com.berijalan.merchant_service.dto.response.*;
import com.berijalan.merchant_service.service.ProductService;
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

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {
    private final MerchantRepository merchantRepository;
    private final MerchantTransactionRepository transactionRepository;
    private final ProductFeignClient productClient;
    private final ProductService productService;

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
            productData = productService.getProductById(request.getProductId());
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
        transaction.setNamaMerchant(merchant.getNamaMerchant());
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
    public Page<MerchantTransactionEntity> getAllTransactions(String userId, String role, Pageable pageable, LocalDate startDate, LocalDate endDate) {
        Page<MerchantTransactionEntity> transactions;
        boolean hasDateFilter = startDate != null && endDate != null;
        LocalDateTime start = hasDateFilter ? startDate.atStartOfDay() : null;
        LocalDateTime end = hasDateFilter ? endDate.atTime(LocalTime.MAX) : null;

        if (role.equals("ADMIN")) {
            transactions = hasDateFilter
                    ? transactionRepository.findAllByTransactionDateBetween(start, end, pageable)
                    : transactionRepository.findAll(pageable);
        } else {
            MerchantEntity merchant = merchantRepository.findByAccountId(userId)
                    .orElseThrow(() -> new DataNotFoundException("Merchant tidak ditemukan"));
            transactions = hasDateFilter
                    ? transactionRepository.findByMerchantIdAndTransactionDateBetween(merchant.getMerchantId(), start, end, pageable)
                    : transactionRepository.findByMerchantId(merchant.getMerchantId(), pageable);
        }

        Set<UUID> merchantIds = transactions.stream()
                .map(MerchantTransactionEntity::getMerchantId)
                .collect(Collectors.toSet());
        Map<UUID, String> merchantNames = merchantRepository.findAllById(merchantIds).stream()
                .collect(Collectors.toMap(MerchantEntity::getMerchantId, MerchantEntity::getNamaMerchant));
        transactions.forEach(tx -> tx.setNamaMerchant(merchantNames.get(tx.getMerchantId())));

        return transactions;
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

    @Override
    public ResAdminStatsDto getAdminStats() {
        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(LocalTime.MAX);

        return ResAdminStatsDto.builder()
                .totalMerchant(merchantRepository.count())
                .totalMerchantAktif(merchantRepository.countByStatus(MerchantEntity.Status.ACTIVE))
                .totalMerchantNonAktif(merchantRepository.countByStatusAndIsDeletedFalse(MerchantEntity.Status.INACTIVE))
                .totalTransaksiHariIni(transactionRepository.countByTransactionDateBetween(startOfDay, endOfDay))
                .totalBerhasilHariIni(transactionRepository.countByStatusAndTransactionDateBetween(MerchantTransactionEntity.Status.SUCCESS, startOfDay, endOfDay))
                .totalGagalHariIni(transactionRepository.countByStatusAndTransactionDateBetween(MerchantTransactionEntity.Status.FAILED,startOfDay,endOfDay))
                .totalTransaksiKeseluruhan(transactionRepository.count())
                .totalBerhasilKeseluruhan(transactionRepository.countByStatus(MerchantTransactionEntity.Status.SUCCESS))
                .totalGagalKeseluruhan(transactionRepository.countByStatus(MerchantTransactionEntity.Status.FAILED))
                .build();
    }

    @Override
    public ResMerchantStatsDto getMerchantStats(String userId, String role, UUID merchantId) {

        UUID targetMerchantId;

        if (role.equals("ADMIN")) {
            if (merchantId == null) {
                throw new BadRequestException("merchantId wajib diisi");
            }
            targetMerchantId = merchantId;
        } else {
            MerchantEntity merchant = merchantRepository.findByAccountId(userId)
                    .orElseThrow(() -> new DataNotFoundException("Merchant tidak ditemukan"));
            targetMerchantId = merchant.getMerchantId();
        }

        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(LocalTime.MAX);

        return ResMerchantStatsDto.builder()
                .totalTransaksiHariIni(transactionRepository.countByMerchantIdAndTransactionDateBetween(targetMerchantId, startOfDay, endOfDay))
                .totalBerhasilHariIni(transactionRepository.countByMerchantIdAndStatusAndTransactionDateBetween(targetMerchantId,MerchantTransactionEntity.Status.SUCCESS, startOfDay, endOfDay))
                .totalGagalHariIni(transactionRepository.countByMerchantIdAndStatusAndTransactionDateBetween(targetMerchantId, MerchantTransactionEntity.Status.FAILED,startOfDay,endOfDay))
                .totalTransaksiKeseluruhan(transactionRepository.countByMerchantId(targetMerchantId))
                .totalBerhasilKeseluruhan(transactionRepository.countByMerchantIdAndStatus(targetMerchantId, MerchantTransactionEntity.Status.SUCCESS))
                .totalGagalKeseluruhan(transactionRepository.countByMerchantIdAndStatus(targetMerchantId, MerchantTransactionEntity.Status.FAILED))
                .build();
    }
}
