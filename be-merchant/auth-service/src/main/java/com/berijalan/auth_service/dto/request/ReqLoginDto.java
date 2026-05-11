package com.berijalan.auth_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ReqLoginDto {
    @NotBlank(message = "Email wajib diisi!")
    private String email;

    @NotBlank(message = "Password wajib diisi!")
    private String password;
}
