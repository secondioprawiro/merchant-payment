package com.berijalan.auth_service.dto.request;

import lombok.Data;

@Data
public class ReqUpdateAccountDto {
    private String email;
    private String password;
}
