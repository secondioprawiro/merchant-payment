package com.berijalan.product_service.service;

import com.berijalan.product_service.dto.response.BaseResponse;
import com.berijalan.product_service.model.Product;

import java.util.List;

public interface ProductService {
    List<Product> getAllProduct(String type);

}
