package com.berijalan.auth_service.client;

import com.berijalan.auth_service.dto.request.ReqCreateMerchantDto;
import com.berijalan.auth_service.dto.response.BaseResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "merchant-service")
public interface MerchantFeignClient {

    @PostMapping("/merchant/internal/create")
    BaseResponse<Void> createMerchant(@RequestBody ReqCreateMerchantDto request);
}
