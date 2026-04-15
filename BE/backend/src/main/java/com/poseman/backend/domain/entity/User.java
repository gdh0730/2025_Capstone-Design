package com.poseman.backend.domain.entity;
import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 관리자(혹은 운영자) 계정 정보.
 */
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue
    private UUID userId;

    @Column(nullable = false, unique = true, length = 50)
    private String username;          // 로그인 아이디

    @Column(nullable = false, length = 255)
    private String passwordHash;      // Bcrypt 해시된 비밀번호

    @Column(nullable = false, length = 20)
    private String role;              // "ADMIN" 또는 "OPERATOR" 등
    
    @Builder.Default
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();  // 계정 생성 시각
}
