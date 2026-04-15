package com.poseman.backend.domain.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.poseman.backend.domain.entity.VideoClip;

/**
 * VideoClip 엔티티를 위한 JPA Repository.
 *  - 이벤트별로 클립 정보 조회용 메서드
 */
public interface VideoClipRepository extends JpaRepository<VideoClip, UUID> {
    VideoClip findByEvent_EventId(UUID eventId);
}
