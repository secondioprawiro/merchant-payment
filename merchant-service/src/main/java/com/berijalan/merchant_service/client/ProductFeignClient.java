package com.berijalan.merchant_service.client;

import com.berijalan.merchant_service.dto.request.ReqTransactionDto;
import com.berijalan.merchant_service.dto.response.BaseResponse;
import com.berijalan.merchant_service.dto.response.ResProductDto;
import com.berijalan.merchant_service.dto.response.ResTransactionDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "product-service", url = "http://localhost:8003")
public interface ProductFeignClient {

    @GetMapping("/product")
    BaseResponse<?> getAllProduct(@RequestParam(required = false) String type);

    @PostMapping("/product/transaction")
    BaseResponse<ResTransactionDto> processTransaction(@RequestBody ReqTransactionDto request);

    @GetMapping("/product/{productId}")
    BaseResponse<ResProductDto> getProductById(@PathVariable String productId);

}
