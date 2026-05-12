package com.berijalan.merchant_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ReqInternalCreateMerchantDto {
    @NotBlank(message = "Account ID wajib diisi!")
    private String accountId;

    @NotBlank(message = "Nama merchant wajib diisi!")
    private String namaMerchant;
}
