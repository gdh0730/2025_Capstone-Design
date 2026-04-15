package com.poseman.backend.domain.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.poseman.backend.domain.entity.Report;
import com.poseman.backend.domain.entity.User;

/**
 * Report 엔티티를 위한 JPA Repository.
 * - 특정 사용자(User)별로 리포트 조회할 때 사용
 */
public interface ReportRepository extends JpaRepository<Report, UUID> {
    List<Report> findByUser(User user);

    // 필요 시 storeId나 type으로도 조회 메서드를 추가 가능
    List<Report> findByStoreIdAndTypeOrderByCreatedAtDesc(String storeId, String type);
}
