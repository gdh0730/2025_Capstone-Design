package com.poseman.backend.exception;

import java.time.format.DateTimeParseException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.validation.ConstraintViolationException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleBadRequest(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<String> handleUnauthorized(UsernameNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<String> handleJwtExpired(ExpiredJwtException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("JWT token expired");
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<String> handleValidation(ConstraintViolationException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Validation failed: " + ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleAll(Exception ex) {
        // 나머지 모든 예외: 내부 서버 에러로 간주
        ex.printStackTrace(); // 로그 남기기
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류: " + ex.getMessage());
    }

    @ExceptionHandler(DateTimeParseException.class)
    public ResponseEntity<String> handleDateTimeParse(DateTimeParseException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("날짜 형식이 올바르지 않습니다 (yyyy-MM-dd 또는 yyyy-MM-dd'T'HH:mm:ss 기준): " + ex.getParsedString());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        StringBuilder errorMessage = new StringBuilder("유효성 검사 실패: ");
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errorMessage.append(error.getField()).append(" - ").append(error.getDefaultMessage()).append("; ");
        });
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMessage.toString());
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<String> handleBadCredentials(BadCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("아이디 또는 비밀번호가 올바르지 않습니다.");
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<String> handleMissingParameter(MissingServletRequestParameterException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("필수 요청 파라미터가 누락되었습니다: " + ex.getParameterName());
    }

    public class ResourceNotFoundException extends RuntimeException {
    }

    public class InvalidRequestException extends RuntimeException {
    }

    public class VideoFetchException extends RuntimeException {
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> handleNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("리소스를 찾을 수 없습니다(404)");
    }

    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<String> handleInvalid(InvalidRequestException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("유효하지 않은 요청입니다(400)");
    }

    @ExceptionHandler(VideoFetchException.class)
    public ResponseEntity<String> handleVideoFetch(VideoFetchException ex) {
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body("VMS 연결 오류가 발생했습니다. 잠시 후 다시 시도하세요: " + ex.getMessage());
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<String> handleIllegalState(IllegalStateException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("잘못된 상태: " + ex.getMessage());
    }

    @ExceptionHandler(UnsupportedOperationException.class)
    public ResponseEntity<String> handleUnsupportedOperation(UnsupportedOperationException ex) {
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).body("지원하지 않는 기능입니다: " + ex.getMessage());
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<String> handleNullPointer(NullPointerException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Null 값이 발생했습니다: " + ex.getMessage());
    }

    @ExceptionHandler(IllegalAccessException.class)
    public ResponseEntity<String> handleIllegalAccess(IllegalAccessException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("접근이 거부되었습니다: " + ex.getMessage());
    }

    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<String> handleSecurity(SecurityException ex) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("보안 오류: " + ex.getMessage());
    }

    @ExceptionHandler(ClassCastException.class)
    public ResponseEntity<String> handleClassCast(ClassCastException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("클래스 캐스팅 오류: " + ex.getMessage());
    }

    @ExceptionHandler(ArithmeticException.class)
    public ResponseEntity<String> handleArithmetic(ArithmeticException ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("산술 오류: " + ex.getMessage());
    }
}
