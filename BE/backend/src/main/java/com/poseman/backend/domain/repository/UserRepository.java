package com.poseman.backend.domain.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.poseman.backend.domain.entity.User;

/**
 * User 엔티티를 위한 JPA Repository.
 *  - 로그인 시 username으로 조회
 *  - 회원가입 시 중복 username 검사
 */
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
}
