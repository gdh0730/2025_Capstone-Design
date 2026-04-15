package com.poseman.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * /api/auth/register 엔드포인트로 회원가입 요청 시 사용하는 DTO.
 */
@Data
public class UserRegisterRequest {
    @NotBlank
    private String username; // 클라이언트에서 보낼 사용자명
    @NotBlank
    @Size(min = 8)
    private String password; // 클라이언트에서 보낼 비밀번호(원문)
}
