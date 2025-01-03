package com.app.sketchbook.user.service;

import com.app.sketchbook.user.exception.CustomAuthenticationException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import java.io.IOException;

//로그인 실패시 경우에 따른 처리를 위한 핸들러
//개발 담당 : 김범철
@Log4j2
public class CustomLoginFailHandler implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        String email = request.getParameter("username");

        //oAuth 로그인 시
        if (exception instanceof OAuth2AuthenticationException) {
            response.sendRedirect("/login?oAuthError");
        } else { //일반 로그인 시

            if (exception.getMessage().equals("메일 인증이 필요합니다.")) {
                response.sendRedirect("/verify?email=" + email);
            } else {
                log.info(exception.getMessage());
                response.sendRedirect("/login?error");
            }
        }
    }
}
