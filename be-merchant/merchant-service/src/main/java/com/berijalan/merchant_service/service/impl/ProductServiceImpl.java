package com.berijalan.merchant_service.service.impl;

import com.berijalan.merchant_service.client.ProductFeignClient;
import com.berijalan.merchant_service.dto.response.BaseResponse;
import com.berijalan.merchant_service.dto.response.ResProductDto;
import com.berijalan.merchant_service.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductFeignClient productClient;

    @Override
    @Cacheable(value = "products-list", key = "#type ?: 'ALL'")
    public BaseResponse<?> getAllProduct(String type) {
        return productClient.getAllProduct(type);
    }

    @Override
    @Cacheable(value = "products", key = "#productId")
    public BaseResponse<ResProductDto> getProductById(String productId) {
        return productClient.getProductById(productId);
    }
}
