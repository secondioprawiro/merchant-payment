package com.berijalan.product_service.controller;

import com.berijalan.product_service.dto.response.BaseResponse;
import com.berijalan.product_service.model.Product;
import com.berijalan.product_service.service.ProductService;
import com.berijalan.product_service.service.impl.ProductServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/product")
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<BaseResponse<?>> getAllProduct(@RequestParam(required = false) String type){
        List<Product> productList = productService.getAllProduct(type);
        return ResponseEntity.ok(BaseResponse.success("Success", productList));
    }


}
