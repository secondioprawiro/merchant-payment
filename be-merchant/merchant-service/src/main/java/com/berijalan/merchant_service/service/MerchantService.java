package com.berijalan.merchant_service.service;

import com.berijalan.merchant_service.dto.request.ReqInternalCreateMerchantDto;
import com.berijalan.merchant_service.dto.request.ReqUpdateMerchantDto;
import com.berijalan.merchant_service.dto.response.ResDetailMerchantDto;
import com.berijalan.merchant_service.dto.response.ResMerchantDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface MerchantService {
    void createMerchant(ReqInternalCreateMerchantDto request);
    Page<ResMerchantDto> getAllMerchants(Pageable pageable, String status, Boolean isDeleted);
    ResDetailMerchantDto getMyMerchant(String userId);
    ResDetailMerchantDto getDetailMerchant(UUID merchantId);
    ResDetailMerchantDto updateMerchant(UUID merchantId, ReqUpdateMerchantDto request, String userId, String role);
    void deleteMerchant(UUID merchantId, String userId, String role);
}
