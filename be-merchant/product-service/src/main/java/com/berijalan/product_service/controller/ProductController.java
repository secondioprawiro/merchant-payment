package com.berijalan.product_service.controller;

import com.berijalan.product_service.dto.request.ReqTransactionDto;
import com.berijalan.product_service.dto.response.BaseResponse;
import com.berijalan.product_service.dto.response.ResTransactionDto;
import com.berijalan.product_service.model.Product;
import com.berijalan.product_service.service.ProductService;
import com.berijalan.product_service.service.impl.ProductServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/product")
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public ResponseEntity<BaseResponse<?>> getAllProduct(@RequestParam(required = false) String type){
        List<Product> productList = productService.getAllProduct(type);
        return ResponseEntity.ok(BaseResponse.success("Success", productList));
    }

    @PostMapping("/transaction")
    public ResponseEntity<BaseResponse<?>> processTransaction(@Valid @RequestBody ReqTransactionDto request){
        ResTransactionDto response = productService.processTransaction(request);
        return ResponseEntity.ok(
               response.getStatus().equals("SUCCESS")
               ? BaseResponse.success("Transaction Success", response) : BaseResponse.failed("Transaction Failed", response)
        );
    }

    @GetMapping("/{productId}")
    public ResponseEntity<BaseResponse<?>> getProductById(
            @PathVariable String productId
    ) {
        Product product = productService.getProductById(productId);
        return ResponseEntity.ok(BaseResponse.success("Success", product));
    }
}
