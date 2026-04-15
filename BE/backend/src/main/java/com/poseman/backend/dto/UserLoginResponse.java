package com.poseman.backend.dto;
import lombok.Data;


/**
 * 로그인 성공 후 응답용 DTO.
 *  /api/auth/login 성공 시 JWT 토큰을 응답하는 DTO.
 */
@Data
public class UserLoginResponse {
    private String token;
    private String username;
    private String role;

}
