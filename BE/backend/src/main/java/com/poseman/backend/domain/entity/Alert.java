package com.poseman.backend.domain.entity;
import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 실시간 알림 로그 (WebSocket, Email 등).
 */
@Entity
@Table(name = "alerts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Alert {

    @Id
    @GeneratedValue
    private UUID alertId;             // 알림 식별자 (PK)

    @OneToOne
    @JoinColumn(name = "event_id", nullable = false, unique = true)
    private EventLog event;           // 어떤 이벤트와 1:1로 연결되는지

    @Column(nullable = false, length = 50)
    private String alertType;         // 예: "WebSocket", "Email", "Popup"

    @Column(nullable = false)
    private LocalDateTime sentTime;   // 알림이 전송된 시각

    @Column(nullable = false)
    private Integer severity;         // 알림 심각도 (1~5)

    @Column(length = 50)
    private String deliveredTo;       // 수신 플랫폼 (예: "WEB_CLIENT", "MOBILE_APP")
}
