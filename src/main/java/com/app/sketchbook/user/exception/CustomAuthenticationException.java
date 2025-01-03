package com.app.sketchbook.user.exception;


import org.springframework.security.core.AuthenticationException;
//로그인 예외에 따른 처리용 예외
//개발 담당 : 김범철
public class CustomAuthenticationException extends AuthenticationException {
    public CustomAuthenticationException(String message) {
        super(message);
    }
}