package com.berijalan.product_service.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Product {
    private String productId;
    private String productName;
    private Long nominal;
    private Long price;
    private String type;
    private String status;
}
