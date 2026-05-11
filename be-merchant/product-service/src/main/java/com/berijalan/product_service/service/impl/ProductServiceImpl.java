package com.berijalan.product_service.service.impl;

import com.berijalan.product_service.dto.request.ReqTransactionDto;
import com.berijalan.product_service.dto.response.ResTransactionDto;
import com.berijalan.product_service.exception.DataNotFoundException;
import com.berijalan.product_service.model.Product;
import com.berijalan.product_service.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final List<Product> products = List.of(
            new Product("PULSA_10K", "PULSA 10.000",10000L, 11000L, "PULSA", "AVAILABLE"),
            new Product("PULSA_25K", "PULSA 25.000",25000L, 26000L, "PULSA",  "AVAILABLE"),
            new Product("PULSA_50K", "PULSA 50.000",10000L, 11000L, "PULSA", "AVAILABLE"),
            new Product("PULSA_100K",  "PULSA 100.000", 10000L, 11000L, "PULSA", "AVAILABLE"),
            new Product("TOKEN_PLN_20K", "TOKEN PLN 20.000", 20000L, 21500L, "PLN", "AVAILABLE"),
            new Product("TOKEN_PLN_50K", "TOKEN PLN 50.000", 50000L, 51500L, "PLN", "AVAILABLE"),
            new Product("TOKEN_PLN_100K", "TOKEN PLN 100.000", 100000L, 101500L, "PLN", "AVAILABLE"),
            new Product("TOKEN_PLN_1000K", "TOKEN PLN 1000.000", 1000000L, 1150000L, "PLN", "NOT AVAILABLE")
    );

    @Override
    public List<Product> getAllProduct(String type) {
        return products.stream()
                .filter(p -> type == null || p.getType().equalsIgnoreCase(type))
                .collect(Collectors.toList());

    }

    @Override
    public Product getProductById(String productId) {
        return products.stream()
                .filter(p -> p.getProductId().equals(productId))
                .findFirst()
                .orElseThrow(() -> new DataNotFoundException("Produk tidak ditemukan"));
    }

    @Override
    public ResTransactionDto processTransaction(ReqTransactionDto request) {
        Product product = products.stream()
                .filter(p -> p.getProductId().equals(request.getProductId()))
                .findFirst()
                .orElseThrow(()-> new DataNotFoundException("Produk tidak ditemukan"));

        boolean isSuccess = Math.random() > 0.3;

        if(isSuccess){
            return ResTransactionDto.builder()
                    .refId("REF" + System.currentTimeMillis())
                    .productId(request.getProductId())
                    .productName(product.getProductName())
                    .nomorTujuan(request.getNomorTujuan())
                    .price(product.getPrice())
                    .status("SUCCESS")
                    .failureReason(null)
                    .build();
        }else{
            List<String> reasons = List.of(
                    "Nomor tujuan tidak valid",
                    "Gangguan jaringan provider",
                    "Stok produk habis",
                    "Timeout dari provider"
            );
            String randomReason = reasons.get((int)(Math.random()* reasons.size()));
            return ResTransactionDto.builder()
                    .refId("REF" + System.currentTimeMillis())
                    .productId(request.getProductId())
                    .productName(product.getProductName())
                    .nomorTujuan(request.getNomorTujuan())
                    .price(product.getPrice())
                    .status("FAILED")
                    .failureReason(randomReason)
                    .build();

        }
    }
}
