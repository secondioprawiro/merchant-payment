package com.berijalan.merchant_service.service;

import com.berijalan.merchant_service.dto.request.ReqInternalCreateMerchantDto;
import com.berijalan.merchant_service.dto.response.ResDetailMerchantDto;
import com.berijalan.merchant_service.dto.response.ResMerchantDto;

import java.util.List;
import java.util.UUID;

public interface MerchantService {
    void createMerchant(ReqInternalCreateMerchantDto request);
    List<ResMerchantDto> getAllMerchants();
    ResDetailMerchantDto getDetailMerchant(UUID merchantId);
}
