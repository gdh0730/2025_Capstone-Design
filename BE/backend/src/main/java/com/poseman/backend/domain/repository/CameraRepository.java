package com.poseman.backend.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.poseman.backend.domain.entity.Camera;

/**
 * Camera 엔티티를 위한 JPA Repository.
 *  - storeId별로 조회할 일이 있을 수 있으므로 findByStoreId 메서드도 추가
 */
public interface CameraRepository extends JpaRepository<Camera, String> {
    // Optional 더할 수도 있습니다만, 상황에 맞춰서 List나 Optional을 결정하세요.
    List<Camera> findByStoreId(String storeId);

    @Query("SELECT DISTINCT c.storeId FROM Camera c")
    List<String> findDistinctStoreIds();
}
