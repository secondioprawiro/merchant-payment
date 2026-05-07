package com.berijalan.auth_service.utils;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResponseJwt {
    private String email;

    private String token;
}
