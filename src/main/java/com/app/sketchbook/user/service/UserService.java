package com.app.sketchbook.user.service;

import com.app.sketchbook.user.DTO.CustomOAuth2User;
import com.app.sketchbook.user.entity.SketchUser;
import com.app.sketchbook.user.repository.UserRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;
import java.util.Random;
//사용자 전체 서비스 관련 파일
//개발 담당 : 김범철
@Service
@RequiredArgsConstructor
public class UserService {

    final UserRepository userRepository;
    private BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();;

    @Autowired
    private JavaMailSender mailSender;

    public SketchUser createUser(String username, String email, String password, String gender){
        SketchUser user = new SketchUser();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setGender(gender);
        user.setEnabled(false);
        user.setAuthCode(UUID.randomUUID().toString());

        userRepository.save(user);

        sendVerificationEmail(user);

        return user;
    }

    public void sendVerificationEmail(SketchUser user) {
        String subject = "Email Verification";
        String message = "Your verification code is: " + user.getAuthCode();

        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
            helper.setTo(user.getEmail());
            helper.setSubject(subject);
            helper.setText(message, true);
            helper.setFrom("mailsender123@naver.com");

            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    public boolean verifyUser(String code) {
        Optional<SketchUser> userOptional = userRepository.findByAuthCode(code);
        if (userOptional.isPresent()) {
            SketchUser user = userOptional.get();
            user.setEnabled(true);
            user.setAuthCode(null);
            userRepository.save(user);
            return true;
        }
        return false;
    }

    public boolean isEmailExists(String email) {
        SketchUser user = userRepository.findByEmail(email);
        if(user==null)
            return false;
        else
            return true;
    }

    public SketchUser findUser(String email){
        SketchUser user = userRepository.findByEmail(email);
        if(user==null)
            return null;
        else
            return user;
    }
    //비밀번호 찾기

    public void findPassword(String email){
        SketchUser user = userRepository.findByEmail(email);
        if(user!=null){
            String tempPassword = generateTempPassword();
            user.setPassword(passwordEncoder.encode(tempPassword));
            user.setUpdate_pw(true);
            userRepository.save(user);
            sendTempPwEmail(email,tempPassword);

        }


    }
    //비밀번호 변경

    public boolean updatePassword(String email, String newPass){
        SketchUser user = userRepository.findByEmail(email);
        if(user!=null){
            user.setPassword(passwordEncoder.encode(newPass));
            user.setUpdate_pw(false);
            userRepository.save(user);
            return true;
        }
        else
            return false;
    }

    private String generateTempPassword() {
        int length = 10;
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder tempPassword = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            tempPassword.append(chars.charAt(random.nextInt(chars.length())));
        }
        return tempPassword.toString();
    }

    public void sendTempPwEmail(String email, String tempPassword) {
        String subject = "Temp Password";
        String message = "Your verification code is: " + tempPassword;
        SketchUser user = userRepository.findByEmail(email);
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
            helper.setTo(user.getEmail());
            helper.setSubject(subject);
            helper.setText(message, true);
            helper.setFrom("mailsender123@naver.com");

            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }


    public SketchUser principalUser(Authentication authentication){
        Object principal = authentication.getPrincipal();
        String email =null;
        if(principal instanceof UserDetails) {
            UserDetails user = (UserDetails) principal;
            email = user.getUsername();
        } else if(principal instanceof OAuth2User){
            OAuth2User userprin =(OAuth2User) principal;
            CustomOAuth2User user = (CustomOAuth2User) userprin;
            email =user.getEmail();
        }
        return userRepository.findByEmail(email);
    }

    //작업자 : 홍제기
    public void updateUser(SketchUser user){
        userRepository.save(user);
    }
}