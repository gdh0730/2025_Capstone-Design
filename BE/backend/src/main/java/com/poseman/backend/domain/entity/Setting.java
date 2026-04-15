package com.poseman.backend.domain.entity;

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
 * 각 카메라별 설정 정보 (민감도, 알림 사용 여부 등).
 */
@Entity
@Table(name = "settings")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Setting {

    @Id
    @GeneratedValue
    private UUID settingId;

    @OneToOne
    @JoinColumn(name = "camera_id", unique = true, nullable = false)
    private Camera camera; // 1:1 관계, FK는 camera_id

    @Column(nullable = false)
    private Float sensitivity; // 민감도 값 (0.0 ~ 1.0)

    @Builder.Default
    @Column(nullable = false)
    private Boolean alertEnabled = true; // 알림 활성화 여부
}
