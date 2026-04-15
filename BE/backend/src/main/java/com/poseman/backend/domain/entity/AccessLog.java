package com.poseman.backend.domain.entity;
import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 관리자(사용자)가 시스템에 접근했을 때의 로그 (감사 로그).
 */
@Entity
@Table(name = "access_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccessLog {

    @Id
    @GeneratedValue
    private UUID logId;               // 접근 로그 식별자 (PK)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonIgnore
    private User user;                // 어떤 관리자가 접근했는지 (N:1 관계)

    @Builder.Default
    @Column(nullable = false, updatable = false)
    private LocalDateTime accessedAt = LocalDateTime.now();  // 접근 시각

    @Column(length = 200)
    private String action;            // 수행된 액션 (예: "영상 재생", "설정 변경")
}
