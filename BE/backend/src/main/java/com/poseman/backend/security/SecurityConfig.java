package com.poseman.backend.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    public SecurityConfig(JwtFilter jwtFilter, UserDetailsServiceImpl userDetailsServiceImpl) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // CSRF 비활성 (JWT 방식으로 stateless 처리)
                .csrf(csrf -> csrf.disable())
                // ─── 2) CORS 활성화: WebConfig에서 정의한 CORS 매핑을 필터 체인에 적용 ───
                .cors(cors -> {
                }) // 빈 람다로 cors 커스터마이즈, .and() 필요 없음
                   // H2 콘솔을 iframe으로 띄우려면 아래 헤더 설정을 해제하거나 sameOrigin으로 설정해야 함
                .headers(headers ->
                // 프레임 옵션 해제 or sameOrigin 허용
                headers.frameOptions(frame -> frame.disable())
                // 혹은: headers.frameOptions().sameOrigin()
                )
                // 세션을 사용하지 않고 JWT로만 stateless 처리
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 인증/인가 설정
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/h2-console/**").permitAll()
                        // 로그인·회원가입, Swagger, WebSocket handshake 엔드포인트만 풀어줌
                        .requestMatchers("/api/auth/**", "/api/events").permitAll()
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**").permitAll() // .hasRole("ADMIN")
                        // STOMP 핸드셰이크 엔드포인트 허용
                        .requestMatchers("/ws/alert/**").permitAll() // 핸드셰이크만 풀어줌
                        // subscribe 할 토픽 경로도 허용 (실제로는 subscribe는 WebSocket 연결 이후에만 가능)
                        .requestMatchers("/app/**", "/topic/**").authenticated() // 실제 구독/메시징은 인증 필요
                        .requestMatchers("/api/report").hasRole("ADMIN") // 리포트 조회는 관리자만 가능
                        // 나머지는 전부 인증 필요
                        .anyRequest().authenticated());

        // JWT 검증 필터를 UsernamePasswordAuthenticationFilter 이전에 삽입
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * 비밀번호 암호화(BCrypt)용 Bean
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /** AuthenticationManager를 SecurityConfig에 등록 */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
