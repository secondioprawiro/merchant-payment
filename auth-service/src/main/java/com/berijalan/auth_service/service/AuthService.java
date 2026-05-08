package com.berijalan.auth_service.service;

import com.berijalan.auth_service.dto.request.ReqCreateAccDto;
import com.berijalan.auth_service.dto.request.ReqLoginDto;
import com.berijalan.auth_service.dto.response.BaseResponse;
import com.berijalan.auth_service.dto.response.ResInternalAccountDto;
import com.berijalan.auth_service.utils.ResponseJwt;

import java.util.UUID;

public interface AuthService {
    ResponseJwt register(ReqCreateAccDto reqCreateAccDto);
    ResponseJwt login(ReqLoginDto reqLoginDto);
    ResInternalAccountDto getAccountById(UUID accountId);
}
