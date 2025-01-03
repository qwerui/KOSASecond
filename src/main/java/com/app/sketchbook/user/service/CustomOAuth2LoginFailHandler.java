package com.app.sketchbook.user.service;


import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

//OAuth2 로그인 실패시 처리를 위한 핸들러
//개발 담당 : 김범철
@Component
public class CustomOAuth2LoginFailHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        // 여기서 실패 처리 로직을 구현합니다.
        // 예를 들어, 실패 메시지를 추가하거나 로그인 페이지로 리다이렉션합니다.
        response.sendRedirect("/login?error" + exception.getMessage());
    }
}
