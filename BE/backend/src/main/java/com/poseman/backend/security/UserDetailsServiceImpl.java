package com.poseman.backend.security;

import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.poseman.backend.domain.entity.User;
import com.poseman.backend.domain.repository.UserRepository;

import lombok.RequiredArgsConstructor;

/**
 * DB에 저장된 User 엔티티를 조회해 Spring Security의 UserDetails로 변환합니다.
 */
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("사용자 없음: " + username));

        // DB의 role 예: "ADMIN" → 스프링 시큐리티 권한은 "ROLE_ADMIN" 형태로 변환
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + user.getRole());

        // Custom UserPrincipal 생성: userId(UUID), username, passwordHash, authorities 전달
        return new UserPrincipal(
                user.getUserId(), // DB에서 가져온 UUID
                user.getUsername(),
                user.getPasswordHash(),
                List.of(authority));
    }
}
