package com.berijalan.product_service.service.impl;

import com.berijalan.product_service.model.Product;
import com.berijalan.product_service.service.ProductService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    private final List<Product> products = List.of(
            new Product("PULSA_10K", 10000L, 11000L, "PULSA", "AVAILABLE"),
            new Product("PULSA_25K", 25000L, 26000L, "PULSA",  "AVAILABLE"),
            new Product("PULSA_50K", 10000L, 11000L, "PULSA", "AVAILABLE"),
            new Product("PULSA_100K",   10000L, 11000L, "PULSA", "AVAILABLE"),
            new Product("TOKEN_PLN_20K",  20000L, 21500L, "PLN", "AVAILABLE"),
            new Product("TOKEN_PLN_50K",  50000L, 51500L, "PLN", "AVAILABLE"),
            new Product("TOKEN_PLN_100K", 100000L, 101500L, "PLN", "AVAILABLE")
    );

    @Override
    public List<Product> getAllProduct(String type) {
        return products.stream()
                .filter(p -> type == null || p.getType().equalsIgnoreCase(type))
                .collect(Collectors.toList());

    }
}
