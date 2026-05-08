package com.berijalan.auth_service.controller;

import com.berijalan.auth_service.dto.request.ReqCreateAccDto;
import com.berijalan.auth_service.dto.request.ReqLoginDto;
import com.berijalan.auth_service.dto.response.BaseResponse;
import com.berijalan.auth_service.dto.response.ResInternalAccountDto;
import com.berijalan.auth_service.service.AuthService;
import com.berijalan.auth_service.utils.ResponseJwt;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<BaseResponse<ResponseJwt>> register(@Valid @RequestBody ReqCreateAccDto request) {
        ResponseJwt data = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(BaseResponse.success("Register berhasil", data));
    }

    @PostMapping("/login")
    public ResponseEntity<BaseResponse<ResponseJwt>> login(@Valid @RequestBody ReqLoginDto request) {
        ResponseJwt data = authService.login(request);
        return ResponseEntity.ok(BaseResponse.success("Login berhasil", data));
    }

    @GetMapping("/internal/account/{accountId}")
    public ResponseEntity<BaseResponse<ResInternalAccountDto>> getAccountById(@PathVariable UUID accountId) {
        ResInternalAccountDto data = authService.getAccountById(accountId);
        return ResponseEntity.ok(BaseResponse.success("Success", data));
    }
}
