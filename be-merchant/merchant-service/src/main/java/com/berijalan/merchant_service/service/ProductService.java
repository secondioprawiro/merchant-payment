package com.berijalan.merchant_service.service;

import com.berijalan.merchant_service.dto.response.BaseResponse;
import com.berijalan.merchant_service.dto.response.ResProductDto;

public interface ProductService {
    BaseResponse getAllProduct(String type);
    BaseResponse<ResProductDto> getProductById(String productId);
}
