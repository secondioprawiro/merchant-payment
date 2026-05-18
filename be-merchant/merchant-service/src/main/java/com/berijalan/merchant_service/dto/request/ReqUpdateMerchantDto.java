package com.berijalan.merchant_service.dto.request;

import com.berijalan.merchant_service.entity.MerchantEntity;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ReqUpdateMerchantDto {
    private String namaMerchant;
    private MerchantEntity.Status status;

    @Email(message = "Format email tidak valid!")
    private String email;

    @Size(min = 8, message = "Password minimal 8 karakter!")
    private String password;
}
