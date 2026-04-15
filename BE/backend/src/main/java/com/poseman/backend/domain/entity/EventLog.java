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
 * 이상행동 이벤트 로그 (stealing, loitering 등).
 */
@Entity
@Table(name = "event_logs", indexes = {
        @jakarta.persistence.Index(name = "idx_event_logs_camera_id", columnList = "camera_id"),
        @jakarta.persistence.Index(name = "idx_event_logs_timestamp", columnList = "timestamp")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventLog {

    @Id
    @GeneratedValue
    private UUID eventId; // 이벤트 식별자 (PK)

    @Column(nullable = false)
    private LocalDateTime timestamp; // 이벤트 발생 시각

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "camera_id")
    @JsonIgnore
    private Camera camera; // 어떤 카메라에서 발생했는지 (N:1 관계)

    @Column(length = 20)
    private String behavior; // 이상행동 유형 (예: "stealing", "loitering")

    @Column(nullable = false)
    private Float confidence; // 모델 신뢰도 (0.0 ~ 1.0)

    @Column(nullable = false)
    private Integer severity; // 위험도 등급 (1~5)
}
