package com.berijalan.merchant_service.service;

import com.berijalan.merchant_service.dto.request.ReqInternalCreateMerchantDto;
import com.berijalan.merchant_service.entity.MerchantEntity;

public interface MerchantService {
    MerchantEntity createMerchant(ReqInternalCreateMerchantDto request);
}
