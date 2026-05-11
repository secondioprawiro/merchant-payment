package com.berijalan.merchant_service.client;

import com.berijalan.merchant_service.client.dto.ReqUpdateAccountDto;
import com.berijalan.merchant_service.client.dto.ResInternalAccountDto;
import com.berijalan.merchant_service.dto.response.BaseResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

@FeignClient(name = "auth-service")
public interface AuthFeignClient {

    @GetMapping("/auth/internal/account/{accountId}")
    BaseResponse<ResInternalAccountDto> getAccountById(@PathVariable UUID accountId);

    @PutMapping("/auth/internal/account/{accountId}")
    BaseResponse<ResInternalAccountDto> updateAccount(@PathVariable UUID accountId,
                                                      @RequestBody ReqUpdateAccountDto request);
}
