package com.berijalan.auth_service.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ReqCreateAccDto {
    @NotBlank(message = "Email wajib diisi!")
    @Email(message = "Format email tidak valid!")
    private String email;

    @NotBlank(message = "Password wajib diisi!")
    @Size(min = 8, message = "Password minimal 8 karakter!")
    private String password;

    @NotBlank(message = "Nama merchant harus diisi")
    private String namaMerchant;
}
