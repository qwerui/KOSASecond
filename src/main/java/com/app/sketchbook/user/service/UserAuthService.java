package com.app.sketchbook.user.service;

import com.app.sketchbook.user.entity.SketchUser;
import com.app.sketchbook.user.exception.CustomAuthenticationException;
import com.app.sketchbook.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

//사용자 로컬 로그인 관련 처리 서비스
//개발 담당 : 김범철
@RequiredArgsConstructor
@Service
public class UserAuthService implements UserDetailsService {
    final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        SketchUser user = this.userRepository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("사용자를 찾을수 없습니다.");
        }
        if (user.getEnabled() == false){
            throw new CustomAuthenticationException("메일 인증이 필요합니다.");
        }
        List<GrantedAuthority> authority = new ArrayList<>();
        authority.add(new SimpleGrantedAuthority("ROLE_USER"));

        return new User(user.getEmail(), user.getPassword(),authority);
    }
}
