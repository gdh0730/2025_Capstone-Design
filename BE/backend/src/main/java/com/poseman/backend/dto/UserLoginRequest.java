package com.poseman.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * /api/auth/login 엔드포인트에 로그인 요청 시 사용하는 DTO.
 */
@Data
public class UserLoginRequest {
    @NotBlank
    private String username; // 클라이언트에서 보낼 사용자명
    @NotBlank
    private String password; // 클라이언트에서 보낼 비밀번호(원문)
}
