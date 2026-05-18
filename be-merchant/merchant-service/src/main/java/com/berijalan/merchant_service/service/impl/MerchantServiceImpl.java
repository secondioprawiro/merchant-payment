package com.berijalan.merchant_service.service.impl;

import com.berijalan.merchant_service.client.AuthFeignClient;
import com.berijalan.merchant_service.client.dto.ReqUpdateAccountDto;
import com.berijalan.merchant_service.client.dto.ResInternalAccountDto;
import com.berijalan.merchant_service.dto.request.ReqInternalCreateMerchantDto;
import com.berijalan.merchant_service.dto.request.ReqUpdateMerchantDto;
import com.berijalan.merchant_service.dto.response.ResDetailMerchantDto;
import com.berijalan.merchant_service.dto.response.ResMerchantDto;
import com.berijalan.merchant_service.entity.MerchantEntity;
import com.berijalan.merchant_service.exception.DataNotFoundException;
import com.berijalan.merchant_service.exception.ForbiddenException;
import com.berijalan.merchant_service.repository.MerchantRepository;
import com.berijalan.merchant_service.service.MerchantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MerchantServiceImpl implements MerchantService {

    private final MerchantRepository merchantRepository;
    private final AuthFeignClient authFeignClient;

    @Override
    public void createMerchant(ReqInternalCreateMerchantDto request) {
        String kodeMerchant = generateKodeMerchant();

        MerchantEntity merchant = MerchantEntity.builder()
                .accountId(request.getAccountId())
                .namaMerchant(request.getNamaMerchant())
                .kodeMerchant(kodeMerchant)
                .status(MerchantEntity.Status.ACTIVE)
                .build();

        merchantRepository.save(merchant);
        log.info("Merchant created: kodeMerchant={}, accountId={}", kodeMerchant, request.getAccountId());
    }

    @Override
    public Page<ResMerchantDto> getAllMerchants(Pageable pageable, String status, Boolean isDeleted) {
        Specification<MerchantEntity> spec = Specification.unrestricted();

        if (status != null) {
            MerchantEntity.Status statusEnum;
            try {
                statusEnum = MerchantEntity.Status.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new com.berijalan.merchant_service.exception.BadRequestException("Status tidak valid. Gunakan: ACTIVE atau INACTIVE");
            }
            spec = spec.and((root, query, cb) -> cb.equal(root.get("status"), statusEnum));
        }

        if (isDeleted != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("isDeleted"), isDeleted));
        }

        return merchantRepository.findAll(spec, pageable)
                .map(m -> new ResMerchantDto(m.getMerchantId(), m.getKodeMerchant(), m.getNamaMerchant(), m.getStatus(), m.getIsDeleted()));
    }

    @Override
    @Cacheable(value = "merchant-by-id", key = "#merchantId")
    public ResDetailMerchantDto getDetailMerchant(UUID merchantId) {
        MerchantEntity merchant = merchantRepository.findByMerchantIdAndIsDeletedFalse(merchantId)
                .orElseThrow(() -> new DataNotFoundException("Merchant tidak ditemukan"));

        ResInternalAccountDto account = authFeignClient
                .getAccountById(UUID.fromString(merchant.getAccountId()))
                .getData();

        return new ResDetailMerchantDto(merchant.getMerchantId(), merchant.getKodeMerchant(), merchant.getNamaMerchant(), account.getEmail());
    }

    @Override
    @Cacheable(value = "merchant-by-user", key = "#userId")
    public ResDetailMerchantDto getMyMerchant(String userId) {
        MerchantEntity merchant = merchantRepository.findByAccountId(userId)
                .orElseThrow(() -> new DataNotFoundException("Merchant tidak ditemukan"));

        ResInternalAccountDto account = authFeignClient
                .getAccountById(UUID.fromString(merchant.getAccountId()))
                .getData();

        log.info("Fetched own merchant: merchantId={}, userId={}", merchant.getMerchantId(), userId);
        return new ResDetailMerchantDto(merchant.getMerchantId(), merchant.getKodeMerchant(), merchant.getNamaMerchant(), account.getEmail());
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "merchant-by-id", key = "#merchantId"),
            @CacheEvict(value = "merchant-by-user", allEntries = true)
    })
    public ResDetailMerchantDto updateMerchant(UUID merchantId, ReqUpdateMerchantDto request, String userId, String role) {
        MerchantEntity merchant = merchantRepository.findByMerchantIdAndIsDeletedFalse(merchantId)
                .orElseThrow(() -> new DataNotFoundException("Merchant tidak ditemukan"));

        if (!"ADMIN".equals(role) && !merchant.getAccountId().equals(userId)) {
            log.warn("Access denied - userId={} tried to update merchantId={}", userId, merchantId);
            throw new ForbiddenException("Access denied");
        }

        if (request.getNamaMerchant() != null && !request.getNamaMerchant().isBlank()) {
            merchant.setNamaMerchant(request.getNamaMerchant());
        }

        if ("ADMIN".equals(role)) {
            if (request.getStatus() != null) {
                merchant.setStatus(request.getStatus());
            }
            merchantRepository.save(merchant);
            ResInternalAccountDto account = authFeignClient
                    .getAccountById(UUID.fromString(merchant.getAccountId()))
                    .getData();
            log.info("Merchant updated by admin: merchantId={}", merchantId);
            return new ResDetailMerchantDto(merchant.getMerchantId(), merchant.getKodeMerchant(), merchant.getNamaMerchant(), account.getEmail());
        }

        merchantRepository.save(merchant);
        ReqUpdateAccountDto accountDto = new ReqUpdateAccountDto(request.getEmail(), request.getPassword());
        ResInternalAccountDto updatedAccount = authFeignClient
                .updateAccount(UUID.fromString(merchant.getAccountId()), accountDto)
                .getData();

        log.info("Merchant updated: merchantId={}", merchantId);
        return new ResDetailMerchantDto(merchant.getMerchantId(), merchant.getKodeMerchant(), merchant.getNamaMerchant(), updatedAccount.getEmail());
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "merchant-by-id", key = "#merchantId"),
            @CacheEvict(value = "merchant-by-user", allEntries = true)
    })
    public void deleteMerchant(UUID merchantId, String userId, String role) {
        MerchantEntity merchant = merchantRepository.findByMerchantIdAndIsDeletedFalse(merchantId)
                .orElseThrow(() -> new DataNotFoundException("Merchant tidak ditemukan"));

        if (!"ADMIN".equals(role) && !merchant.getAccountId().equals(userId)) {
            log.warn("Access denied - userId={} tried to delete merchantId={}", userId, merchantId);
            throw new ForbiddenException("Access denied");
        }

        merchant.setIsDeleted(true);
        merchant.setStatus(MerchantEntity.Status.INACTIVE);
        merchant.setDeletedAt(LocalDateTime.now());
        merchantRepository.save(merchant);
        log.info("Merchant deleted: merchantId={}", merchantId);
    }

    private String generateKodeMerchant() {
        String kode;
        do {
            kode = "MCH-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        } while (merchantRepository.existsByKodeMerchant(kode));
        return kode;
    }
}
