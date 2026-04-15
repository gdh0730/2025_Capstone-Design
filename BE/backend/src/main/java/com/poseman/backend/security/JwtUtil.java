package com.poseman.backend.security;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

import javax.crypto.SecretKey;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;

@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtUtil {
    // 실제 운영 환경에서는 이 값을 환경변수나 externalized configuration으로 분리해야 합니다.
    private String secret; // application.yml의 jwt.secret이 바인딩됨
    private long expiration;

    private SecretKey secretKey; // 실제 키 객체

    // Spring이 property를 바인딩할 때 호출할 setter
    public void setSecret(String secret) {
        this.secret = secret;
    }

    public void setExpiration(long expiration) {
        this.expiration = expiration;
    }

    @PostConstruct
    public void init() {
        // secret, expiration 값이 들어와 있으면 SecretKey 생성
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * username, role, 그리고 userId(UUID)를 클레임에 담아 JWT를 생성합니다.
     */
    public String generateToken(UUID userId, String username, String role) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .setSubject(username) // subject는 username
                .claim("role", role) // 권한 정보
                .claim("userId", userId.toString()) // 사용자 식별자(UUID)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * 주어진 토큰을 파싱하여 Claims를 반환합니다.
     */
    public Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
