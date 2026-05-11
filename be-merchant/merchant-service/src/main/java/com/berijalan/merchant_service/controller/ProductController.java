package com.berijalan.merchant_service.controller;

import com.berijalan.merchant_service.service.ProductService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/product")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @GetMapping()
    public ResponseEntity getAllProduct(
            @RequestParam(required = false) String type
    ) {
        return ResponseEntity.ok(productService.getAllProduct(type));
    }

    @GetMapping("/{productId}")
    public ResponseEntity getProductById(@PathVariable String productId) {
        return ResponseEntity.ok(productService.getProductById(productId));
    }
}
