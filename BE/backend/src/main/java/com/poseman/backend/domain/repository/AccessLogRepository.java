package com.poseman.backend.domain.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.poseman.backend.domain.entity.AccessLog;
import com.poseman.backend.domain.entity.User;

/**
 * AccessLog 엔티티를 위한 JPA Repository.
 *  - 사용자별 또는 날짜별 접근 로그 조회 시 유용
 */
public interface AccessLogRepository extends JpaRepository<AccessLog, UUID> {
    // 예시: 특정 사용자(User)별로 접근 로그 조회
    List<AccessLog> findByUser(User user);
}
