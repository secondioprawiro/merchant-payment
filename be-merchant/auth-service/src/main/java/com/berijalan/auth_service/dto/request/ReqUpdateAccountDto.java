package com.berijalan.auth_service.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ReqUpdateAccountDto {
    @Email(message = "Format email tidak valid!")
    private String email;

    @Size(min = 8, message = "Password minimal 8 karakter!")
    private String password;
}
