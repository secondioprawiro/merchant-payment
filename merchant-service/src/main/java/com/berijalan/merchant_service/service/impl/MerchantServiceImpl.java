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
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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
    }

    @Override
    public List<ResMerchantDto> getAllMerchants() {
        return merchantRepository.findAll().stream()
                .map(m -> new ResMerchantDto(m.getKodeMerchant(), m.getNamaMerchant()))
                .collect(Collectors.toList());
    }

    @Override
    public ResDetailMerchantDto getDetailMerchant(UUID merchantId) {
        MerchantEntity merchant = merchantRepository.findById(merchantId)
                .orElseThrow(() -> new DataNotFoundException("Merchant tidak ditemukan"));

        ResInternalAccountDto account = authFeignClient
                .getAccountById(UUID.fromString(merchant.getAccountId()))
                .getData();

        return new ResDetailMerchantDto(merchant.getNamaMerchant(), account.getEmail());
    }

    @Override
    public ResDetailMerchantDto updateMerchant(UUID merchantId, ReqUpdateMerchantDto request, String userId, String role) {
        MerchantEntity merchant = merchantRepository.findById(merchantId)
                .orElseThrow(() -> new DataNotFoundException("Merchant tidak ditemukan"));

        if (!"ADMIN".equals(role) && !merchant.getAccountId().equals(userId)) {
            throw new ForbiddenException("Access denied");
        }

        if (request.getNamaMerchant() != null && !request.getNamaMerchant().isBlank()) {
            merchant.setNamaMerchant(request.getNamaMerchant());
            merchantRepository.save(merchant);
        }

        ReqUpdateAccountDto accountDto = new ReqUpdateAccountDto(request.getEmail(), request.getPassword());
        ResInternalAccountDto updatedAccount = authFeignClient
                .updateAccount(UUID.fromString(merchant.getAccountId()), accountDto)
                .getData();

        return new ResDetailMerchantDto(merchant.getNamaMerchant(), updatedAccount.getEmail());
    }

    private String generateKodeMerchant() {
        String kode;
        do {
            kode = "MCH-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        } while (merchantRepository.existsByKodeMerchant(kode));
        return kode;
    }
}
