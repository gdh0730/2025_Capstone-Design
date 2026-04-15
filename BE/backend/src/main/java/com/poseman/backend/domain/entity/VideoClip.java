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
 * 이벤트 발생 시 저장된 영상 클립 정보.
 */
@Entity
@Table(name = "video_clips")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VideoClip {

    @Id
    @GeneratedValue
    private UUID clipId; // 클립 식별자 (PK)

    @OneToOne
    @JoinColumn(name = "event_id", nullable = false, unique = true)
    private EventLog event; // 어떤 이벤트와 1:1로 연결되는지

    @Column(nullable = false, length = 255)
    private String filePath; // 영상 파일 경로 (예: "/videos/store3/cam3/2025-05-30/15-42.mp4")

    @Column(nullable = false)
    private LocalDateTime startTime; // 클립 시작 시각

    @Column(nullable = false)
    private LocalDateTime endTime; // 클립 종료 시각
}
