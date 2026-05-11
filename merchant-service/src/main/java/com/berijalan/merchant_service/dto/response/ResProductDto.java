package com.berijalan.merchant_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResProductDto {
    private String productId;
    private Long nominal;
    private Long price;
    private String type;
    private String status;
}
