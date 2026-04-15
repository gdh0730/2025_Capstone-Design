package com.poseman.backend.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.poseman.backend.dto.UserLoginRequest;
import com.poseman.backend.dto.UserLoginResponse;
import com.poseman.backend.dto.UserRegisterRequest;
import com.poseman.backend.security.JwtUtil;
import com.poseman.backend.security.UserPrincipal;
import com.poseman.backend.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserService userService;

    // 1) 로그인 처리 (/api/auth/login)
    @PostMapping("/login")
    public ResponseEntity<UserLoginResponse> login(@RequestBody @Valid UserLoginRequest request) {
        // AuthenticationManager를 이용해 사용자 인증 시도
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        // 인증에 성공하면 Principal(UserPrincipal)을 꺼내고, 토큰 생성
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        UUID userId = userPrincipal.getUserId();
        String username = userPrincipal.getUsername();
        String role = userPrincipal.getAuthorities().iterator()
                .next().getAuthority().replace("ROLE_", "");

        // generateToken에 userId(UUID), username, role 세 개를 넘겨야 함
        String token = jwtUtil.generateToken(userId, username, role);

        // UserLoginResponse DTO를 “setXxx(...)” 방식으로 채워서 반환
        UserLoginResponse response = new UserLoginResponse();
        response.setToken(token);
        response.setUsername(username);
        response.setRole(role);

        return ResponseEntity.ok(response);
    }

    // 3) 회원가입 처리 (/api/auth/register)
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid UserRegisterRequest request) {
        userService.register(request);
        return ResponseEntity.ok().build();
    }
}
