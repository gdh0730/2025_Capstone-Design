package com.poseman.backend.domain.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.poseman.backend.domain.entity.EventLog;

/**
 * EventLog 엔티티를 위한 JPA Repository.
 * - 날짜 범위 또는 행동별 조회용 메서드 선언
 */
public interface EventLogRepository extends JpaRepository<EventLog, UUID> {
        // 행동 유형별 조회
        List<EventLog> findByBehaviorOrderByTimestampDesc(String behavior);

        // 예시: 특정 기간 동안 모든 이벤트 조회
        @EntityGraph(attributePaths = { "camera" })
        List<EventLog> findByTimestampBetweenOrderByTimestampDesc(LocalDateTime start, LocalDateTime end);

        // 모든 이벤트 조회
        // 이 메서드는 JpaRepository가 기본적으로 제공하므로
        // 추가 구현 없이도 모든 이벤트를 조회할 수 있습니다.

        // 예시: 특정 기간 동안 특정 행동을 한 이벤트 조회
        List<EventLog> findByBehaviorAndTimestampBetweenOrderByTimestampDesc(
                        String behavior,
                        LocalDateTime start,
                        LocalDateTime end);

        // 예시: 특정 기간 동안 특정 카메라에서 발생한 이벤트 조회
        @EntityGraph(attributePaths = { "camera" })
        List<EventLog> findByCamera_CameraIdAndTimestampBetweenOrderByTimestampDesc(
                        String cameraId,
                        LocalDateTime start,
                        LocalDateTime end);

        // 매장 단위 리포트에 사용할 가능성: storeId와 timestamp 범위 조회
        // (연관이 EventLog→Camera→storeId 로 걸리므로 JPQL을 쓰거나
        // findByCamera_StoreIdAndTimestampBetween 과 같은 네이밍도 가능)
        List<EventLog> findByCamera_StoreIdAndTimestampBetweenOrderByTimestampDesc(
                        String storeId,
                        LocalDateTime start,
                        LocalDateTime end);
}
