package com.berijalan.auth_service.repository;

import com.berijalan.auth_service.entity.AuthEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AuthRepository extends JpaRepository<AuthEntity, UUID> {
    Boolean existsByEmail(String email);
    Optional<AuthEntity> findByEmail(String email);
}
