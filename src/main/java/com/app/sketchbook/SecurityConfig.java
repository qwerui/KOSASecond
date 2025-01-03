package com.app.sketchbook;


import com.app.sketchbook.user.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import static org.springframework.security.config.Customizer.withDefaults;

//사용자 인증 관리를위한 스프링시큐리티 필터체인
//개발 담당 : 김범철
@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfig {


    private final OAuth2UserService oAuth2UserService;
    private final CustomLoginSuccessHandler customLoginSuccessHandler;
    private final UserService userService;
    private final ConnectionLogService connectionLogService;


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf((csrf) -> csrf.disable());

        http
                .formLogin((login) -> login.disable());

        http
                .httpBasic((basic) -> basic.disable());

        http
                .oauth2Login((oauth2) -> oauth2
                        .loginPage("/login")
                        .failureHandler(new CustomLoginFailHandler())
                        .successHandler(new CustomOAuth2LoginSuccessHandler(connectionLogService, userService))
                        .userInfoEndpoint((userInfoEndpointConfig) -> userInfoEndpointConfig
                                .userService(oAuth2UserService)));
        http
                .formLogin((formLogin) -> formLogin
                        .loginPage("/login")
                        .successHandler(customLoginSuccessHandler)
                        .failureHandler(new CustomLoginFailHandler())) // 로그인 실패 시 CustomAuthenticationFailureHandler 사용

                .logout((logout) -> logout
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                        .logoutSuccessUrl("/login")
                        .invalidateHttpSession(true))
        ;

        http
                .authorizeHttpRequests((auth) -> auth
                        .requestMatchers("/login","/join","/join_success","/verify","/verify-success","/verify-failure","/check-email","/resend-activation-email"
                        ,"/find-account"
                        ,"/find-password"
                        ,"/updatepassword"
                        ,"/send-pass-email","/joinform").permitAll()
                        .requestMatchers("/css/**","/js/**","/img/**").permitAll()
                        .anyRequest().authenticated());

        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}