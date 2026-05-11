package com.berijalan.auth_service.service.impl;

import com.berijalan.auth_service.client.MerchantFeignClient;
import com.berijalan.auth_service.dto.request.ReqCreateAccDto;
import com.berijalan.auth_service.dto.request.ReqCreateMerchantDto;
import com.berijalan.auth_service.dto.request.ReqLoginDto;
import com.berijalan.auth_service.dto.request.ReqUpdateAccountDto;
import com.berijalan.auth_service.dto.response.ResInternalAccountDto;
import com.berijalan.auth_service.entity.AuthEntity;
import com.berijalan.auth_service.exception.BadRequestException;
import com.berijalan.auth_service.exception.DataNotFoundException;
import com.berijalan.auth_service.repository.AuthRepository;
import com.berijalan.auth_service.service.AuthService;
import com.berijalan.auth_service.utils.JwtUtil;
import com.berijalan.auth_service.utils.ResponseJwt;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final AuthRepository authRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final MerchantFeignClient merchantFeignClient;

    public AuthServiceImpl(AuthRepository authRepository, JwtUtil jwtUtil, PasswordEncoder passwordEncoder,
                           MerchantFeignClient merchantFeignClient) {
        this.authRepository = authRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
        this.merchantFeignClient = merchantFeignClient;
    }

    @Override
    public ResponseJwt register(ReqCreateAccDto reqCreateAccDto) {
        if (authRepository.existsByEmail(reqCreateAccDto.getEmail())) {
            throw new BadRequestException("Email already exists");
        }

        AuthEntity authEntity = new AuthEntity();
        authEntity.setEmail(reqCreateAccDto.getEmail());
        authEntity.setPassword(passwordEncoder.encode(reqCreateAccDto.getPassword()));
        authEntity.setRole(AuthEntity.Role.USER);
        AuthEntity saved = authRepository.save(authEntity);

        merchantFeignClient.createMerchant(new ReqCreateMerchantDto(
                saved.getAccountId().toString(),
                reqCreateAccDto.getNamaMerchant()
        ));

        String token = jwtUtil.generateToken(saved.getEmail(), saved.getAccountId().toString());
        return new ResponseJwt(saved.getEmail(), token);
    }

    @Override
    public ResponseJwt login(ReqLoginDto reqLoginDto) {
        AuthEntity authEntity = authRepository.findByEmail(reqLoginDto.getEmail())
                .orElseThrow(() -> new DataNotFoundException("Email tidak ditemukan"));

        if (!passwordEncoder.matches(reqLoginDto.getPassword(), authEntity.getPassword())) {
            throw new BadRequestException("Password salah");
        }

        String token = jwtUtil.generateToken(authEntity.getEmail(), authEntity.getAccountId().toString());
        return new ResponseJwt(authEntity.getEmail(), token);
    }

    @Override
    public ResInternalAccountDto getAccountById(UUID accountId) {
        AuthEntity authEntity = authRepository.findById(accountId)
                .orElseThrow(() -> new DataNotFoundException("Account tidak ditemukan"));
        return new ResInternalAccountDto(authEntity.getEmail());
    }

    @Override
    public ResInternalAccountDto updateAccount(UUID accountId, ReqUpdateAccountDto request) {
        AuthEntity authEntity = authRepository.findById(accountId)
                .orElseThrow(() -> new DataNotFoundException("Account tidak ditemukan"));

        if (request.getEmail() != null && !request.getEmail().equals(authEntity.getEmail())) {
            if (authRepository.existsByEmailAndAccountIdNot(request.getEmail(), accountId)) {
                throw new BadRequestException("Email sudah digunakan");
            }
            authEntity.setEmail(request.getEmail());
        }

        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            authEntity.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        AuthEntity saved = authRepository.save(authEntity);
        return new ResInternalAccountDto(saved.getEmail());
    }
}
