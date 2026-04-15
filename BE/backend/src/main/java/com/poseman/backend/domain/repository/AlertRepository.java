package com.poseman.backend.domain.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.poseman.backend.domain.entity.Alert;

/**
 * Alert 엔티티를 위한 JPA Repository.
 *  - 이벤트별로 알림 이력을 조회하거나 삭제할 일 대비
 */
public interface AlertRepository extends JpaRepository<Alert, UUID> {
    // 예시: 특정 이벤트(eventId)에 매핑된 알림 조회
    Alert findByEvent_EventId(UUID eventId);
}
