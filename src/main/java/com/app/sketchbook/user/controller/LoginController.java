package com.app.sketchbook.user.controller;

import com.app.sketchbook.user.DTO.CreateUserForm;
import com.app.sketchbook.user.DTO.PasswordUpdate;
import com.app.sketchbook.user.entity.SketchUser;
import com.app.sketchbook.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Log4j2
@RequiredArgsConstructor
@Controller
public class LoginController {
    //개발 담당 : 김범철
    private final UserService userService;

    @GetMapping("/login") //로그인 화면
    public String UserLogin(){
        return "login";
    }

    @PostMapping("/resend-activation-email") // 이메일 인증메일 재전송기능
    public String resendActivationEmail(@RequestParam("email") String email, RedirectAttributes redirectAttributes) {
        SketchUser user = userService.findUser(email);
        try {
            userService.sendVerificationEmail(user);
            redirectAttributes.addFlashAttribute("message", "인증 코드 이메일이 재발송되었습니다. 이메일을 확인해 주세요.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "이메일 전송에 실패했습니다. 다시 시도해 주세요.");
        }
        return "redirect:/verify";
    }

    @GetMapping("/find-account") //패스워드 분실시 아이디를 통한 임시 패스워드 발급
    public String findAccount(){
        return "find-account";
    }

    @PostMapping("/find-account") //아이디입력받은후 임시패스워드 발급 단계로 전달
    public String findAccountPassword(@RequestParam("email") String email, RedirectAttributes redirectAttributes){
        redirectAttributes.addAttribute(email);
        return "redirect:/find-password";
    }

    @GetMapping("/find-password") //아이디 존재 여부 체크후 패스워드 발급 단계로 전달
    public String joinSuccess(@RequestParam(value = "email", required = false) String email, Model model) {
        if (email != null) {
            SketchUser user = userService.findUser(email);
            model.addAttribute("user", user);
        }
        return "find-password";
    }

    //임시패스워드 메일발송
    @PostMapping("/send-pass-email")
    public String sendPassEmail(@RequestParam("email") String email, RedirectAttributes redirectAttributes) {
        SketchUser user = userService.findUser(email);
        log.info(email);
        try {
            userService.findPassword(email);
            redirectAttributes.addFlashAttribute("message", "임시 패스워드가 이메일로 발송되었습니다. 이메일을 확인해 주세요.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "이메일 전송에 실패했습니다. 다시 시도해 주세요.");
        }
        return "redirect:/find-password";
    }

    @GetMapping("/updatepassword") //임시패스워드로 로그인한 계정의 패스워드 변경
    public String insertUpdatePw(HttpSession session, Model model, PasswordUpdate passwordUpdate) {
        String username = session.getAttribute("username").toString();
        if (username != null) {
            model.addAttribute("email", username);
        }
        return "updatepassword";
    }

    @PostMapping("/updatepassword")//패스워드 검증 및 데이터 update
    public String updatePass(@Valid PasswordUpdate passwordUpdate, BindingResult bindingResult,
                             HttpServletRequest request, RedirectAttributes redirectAttributes, @RequestParam("email") String email) {
        if (bindingResult.hasErrors()) {
            return "updatepassword";
        }

        if (!passwordUpdate.getPassword1().equals(passwordUpdate.getPassword2())) {
            bindingResult.rejectValue("password2", "passwordInCorrect",
                    "2개의 패스워드가 일치하지 않습니다.");
            return "updatepassword";
        }

        userService.updatePassword(email, passwordUpdate.getPassword1());
        //패스워드 변경후 재로그인
        request.getSession().invalidate();
        //return "redirect:/main";
        return "redirect:/login";
    }

}
