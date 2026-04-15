package com.poseman.backend.domain.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.poseman.backend.domain.Enum.Period;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 사용자 분석 리포트 (일간, 주간 통계 저장용).
 */
@Entity
@Table(name = "reports", indexes = {
                @jakarta.persistence.Index(name = "idx_reports_store_id", columnList = "store_id"),
                @jakarta.persistence.Index(name = "idx_reports_type", columnList = "type"),
                @jakarta.persistence.Index(name = "idx_reports_created_at", columnList = "created_at")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Report {

        @Id
        @GeneratedValue
        private UUID reportId; // 리포트 식별자 (PK)

        @Column(nullable = false, length = 50)
        private String storeId; // 매장 식별자 (FK, 카메라와 연관)

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "user_id")
        @JsonIgnore
        private User user; // 리포트 생성자 (관리자)

        @Builder.Default
        @Column(nullable = false, updatable = false)
        private LocalDateTime createdAt = LocalDateTime.now(); // 생성 시각

        @Enumerated(EnumType.STRING)
        @Column(nullable = false, length = 20)
        private Period type; // "daily", "weekly" 등

        @Lob
        @Column(nullable = false) // columnDefinition = "jsonb")
        private String dataJson; // 요약 통계 JSON (맵을 문자열로 변환하여 저장)

        /**
         * DTO 용으로 Map<String,Object>를 JSON 문자열로 변환하여 저장하는 헬퍼 메서드.
         */
        /*
         * public void setDataJsonFromMap(Map<String, Object> map) {
         * try {
         * this.dataJson = new ObjectMapper().writeValueAsString(map);
         * } catch (JsonProcessingException e) {
         * throw new RuntimeException("JSON 변환 오류", e);
         * }
         * }
         */
}
