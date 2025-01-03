package com.app.sketchbook.user.controller;

import com.app.sketchbook.user.DTO.CreateUserForm;
import com.app.sketchbook.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;

@RequiredArgsConstructor
@Controller
public class JoinController {
    //개발 담당 : 김범철
    private final UserService userService;
    private int number; // 이메일 인증 숫자를 저장하는 변수

    @GetMapping("/join") //회원가입
    public String joinUs(CreateUserForm createUserForm){
        return "join";
    }


    @PostMapping("/join") //로컬 회원가입 시 입력값 검증
    public String signup(@Valid CreateUserForm createUserForm, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "join";
        }

        if (!createUserForm.getPassword1().equals(createUserForm.getPassword2())) {
            bindingResult.rejectValue("password2", "passwordInCorrect",
                    "2개의 패스워드가 일치하지 않습니다.");
            return "join";
        }

        userService.createUser(createUserForm.getUsername(),
                createUserForm.getEmail(), createUserForm.getPassword1(), createUserForm.getGender());

        String email = createUserForm.getEmail();
        redirectAttributes.addAttribute("email", email); // 이메일 인증을위한 파라미터 추가
        //return "redirect:/main";
        return "redirect:/join_success";
    }

    @GetMapping("/join_success") //가입완료 BUT 비활성계정
    public String joinSuccess(@RequestParam(value = "email", required = false) String email, Model model) {
        // 이메일 값을 모델에 추가하여 뷰에서 사용 가능
        if (email != null) {
            model.addAttribute("email", email); //이메일 검증용 값 전달
        }
        return "join_success";
    }

    @GetMapping("/verify") // 이메일 인증
    public String showVerificationForm() {
        return "/verify";
    }

    @PostMapping("/verify") //이메일 인증값에 따른 단계 설정
    public String verifyUser(@RequestParam("code") String code) {
        if (userService.verifyUser(code)) {
            return "/verify-success";
        } else {
            return "/verify-failure";
        }
    }

    @GetMapping("/check-email") //OAuth2 중복 가입 방지를 위한 계정 체크
    @ResponseBody
    public boolean checkEmail(@RequestParam("email") String email) {
        return userService.isEmailExists(email);
    }

}
