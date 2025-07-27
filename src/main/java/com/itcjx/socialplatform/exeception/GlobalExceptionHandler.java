package com.itcjx.socialplatform.exeception;

import com.itcjx.socialplatform.util.Result;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.file.AccessDeniedException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 拦截所有异常并统一返回 JSON
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Result<?>> handleException(HttpServletRequest request, Exception ex) {
        System.out.println("【全局异常】" + ex.getClass().getName() + ": " + ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Result.error(Result.ErrorCode.SERVER_ERROR, ex.getMessage()));
    }

    // 拦截 JWT 解析错误
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Result<?>> handleJwtException(HttpServletRequest request, IllegalArgumentException ex) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(Result.error(Result.ErrorCode.UNAUTHORIZED, "无效的 Token"));
    }

    // 拦截访问拒绝异常（403错误）
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Result<?>> handleAccessDeniedException(AccessDeniedException ex) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(Result.error(Result.ErrorCode.FORBIDDEN, "访问被拒绝"));
    }
}
