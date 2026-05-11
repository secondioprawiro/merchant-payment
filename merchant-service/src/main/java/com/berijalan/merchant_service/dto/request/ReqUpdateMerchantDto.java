package com.berijalan.merchant_service.dto.request;

import lombok.Data;

@Data
public class ReqUpdateMerchantDto {
    private String namaMerchant;
    private String email;
    private String password;
}
