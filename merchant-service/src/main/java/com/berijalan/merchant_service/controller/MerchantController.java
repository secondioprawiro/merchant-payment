package com.berijalan.merchant_service.controller;

import com.berijalan.merchant_service.dto.request.ReqInternalCreateMerchantDto;
import com.berijalan.merchant_service.dto.response.BaseResponse;
import com.berijalan.merchant_service.service.MerchantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/merchant")
@RequiredArgsConstructor
public class MerchantController {

    private final MerchantService merchantService;

    @PostMapping("/internal/create")
    public ResponseEntity<BaseResponse<Void>> createMerchant(@RequestBody ReqInternalCreateMerchantDto request) {
        merchantService.createMerchant(request);
        return ResponseEntity.ok(BaseResponse.success("Merchant created", null));
    }
}
