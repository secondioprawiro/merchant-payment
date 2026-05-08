package com.berijalan.auth_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResCreateAccDto {
    private String accountId;
    private String email;
    private String role;
    private LocalDateTime createdAt;
}
