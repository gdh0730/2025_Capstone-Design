package com.poseman.backend.service;

import java.time.LocalDateTime;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.poseman.backend.domain.entity.User;
import com.poseman.backend.domain.repository.UserRepository;
import com.poseman.backend.dto.UserRegisterRequest;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 회원가입 처리 (/api/auth/register)
     *  1) 유저명 중복 체크
     *  2) 비밀번호 Bcrypt 해시 후 저장
     *  3) 기본 권한 “ADMIN”으로 설정
     *
     * @param request - { username, password }
     */
    @Transactional
    public void register(UserRegisterRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("이미 존재하는 사용자명입니다.");
        }

        User user = new User();
        // @GeneratedValue가 없으면 직접 할당, 있으면 생략 가능
        //user.setUserId(UUID.randomUUID());
        user.setUsername(request.getUsername());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setRole("ADMIN");  // 기본 ADMIN 권한 부여
        user.setCreatedAt(LocalDateTime.now());

        userRepository.save(user);
    }

    /**
     * 로그인 처리 – Service 단에서 BCrypt 검사 예시 제공
     * 실제 인증은 SecurityConfig와 AuthController에서 AuthenticationManager로 처리
     *
     * @param username    - 사용자명
     * @param rawPassword - 평문 비밀번호
     * @return Optional<User> (일치 시 사용자 정보, 불일치 시 empty)
     */
    /*
    public Optional<User> authenticate(String username, String rawPassword) {
        return userRepository.findByUsername(username)
                .filter(user -> passwordEncoder.matches(rawPassword, user.getPasswordHash()));
    }
    */
}
