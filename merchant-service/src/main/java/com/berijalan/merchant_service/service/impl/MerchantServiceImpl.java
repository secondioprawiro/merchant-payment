package com.berijalan.merchant_service.service.impl;

import com.berijalan.merchant_service.dto.request.ReqInternalCreateMerchantDto;
import com.berijalan.merchant_service.entity.MerchantEntity;
import com.berijalan.merchant_service.repository.MerchantRepository;
import com.berijalan.merchant_service.service.MerchantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MerchantServiceImpl implements MerchantService {

    private final MerchantRepository merchantRepository;

    @Override
    public MerchantEntity createMerchant(ReqInternalCreateMerchantDto request) {
        String kodeMerchant = generateKodeMerchant();

        MerchantEntity merchant = MerchantEntity.builder()
                .accountId(request.getAccountId())
                .namaMerchant(request.getNamaMerchant())
                .kodeMerchant(kodeMerchant)
                .status(MerchantEntity.Status.ACTIVE)
                .build();

        return merchantRepository.save(merchant);
    }

    private String generateKodeMerchant() {
        String kode;
        do {
            kode = "MCH-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        } while (merchantRepository.existsByKodeMerchant(kode));
        return kode;
    }
}
