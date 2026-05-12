package com.berijalan.auth_service.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ReqLoginDto {
    @NotBlank(message = "Email wajib diisi!")
    @Email(message = "Format email tidak valid!")
    private String email;

    @NotBlank(message = "Password wajib diisi!")
    private String password;
}
