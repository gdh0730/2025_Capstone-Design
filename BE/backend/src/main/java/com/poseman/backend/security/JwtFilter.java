package com.poseman.backend.security;

import java.io.IOException;
import java.util.UUID;

import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsServiceImpl userDetailsService; // UserDetailsServiceImpl 타입

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            try {
                Claims claims = jwtUtil.extractClaims(token);
                UUID userId = UUID.fromString(claims.get("userId", String.class));
                String username = claims.getSubject();
                String role = claims.get("role", String.class);

                // UserDetails 대신 UserPrincipal 객체를 직접 생성해도 된다.
                // 또는 userDetailsService.loadUserByUsername(username)을 호출해 UserPrincipal 얻는 방법:
                UserPrincipal userPrincipal = (UserPrincipal) userDetailsService.loadUserByUsername(username);

                // UserDetailsServiceImpl에서 반환된 UserPrincipal 안에 userId가 이미 담겨 있으므로,
                // "claims.get("userId")"와 일치하는지 확인할 수도 있고,
                // 생략해도 무방하다. (DB 재조회는 중복이므로 한 번만 꺼내는 걸 추천)
                // userPrincipal.getUserId() 와 userId가 일치하는지 확인해도 된다.

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userPrincipal,
                        null,
                        userPrincipal.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } catch (ExpiredJwtException ex) {
                logger.warn("만료된 JWT 토큰: " + ex.getMessage());
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "만료된 JWT 토큰입니다.");
                return;
            } catch (JwtException | IllegalArgumentException ex) {
                logger.warn("유효하지 않은 JWT 토큰: " + ex.getMessage());
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "유효하지 않은 JWT 토큰입니다.");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
