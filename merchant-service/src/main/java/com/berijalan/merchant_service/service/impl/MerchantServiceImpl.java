package com.berijalan.merchant_service.service.impl;

import com.berijalan.merchant_service.client.AuthFeignClient;
import com.berijalan.merchant_service.client.dto.ResInternalAccountDto;
import com.berijalan.merchant_service.dto.request.ReqInternalCreateMerchantDto;
import com.berijalan.merchant_service.dto.response.ResDetailMerchantDto;
import com.berijalan.merchant_service.dto.response.ResMerchantDto;
import com.berijalan.merchant_service.entity.MerchantEntity;
import com.berijalan.merchant_service.exception.DataNotFoundException;
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

    private String generateKodeMerchant() {
        String kode;
        do {
            kode = "MCH-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        } while (merchantRepository.existsByKodeMerchant(kode));
        return kode;
    }
}
