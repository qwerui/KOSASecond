package com.app.sketchbook.user.controller;

import com.app.sketchbook.user.DTO.ModifyUserForm;
import com.app.sketchbook.user.service.ConnectionLogService;
import com.app.sketchbook.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

//작업자 : 홍제기
@Log
@Controller
@RequestMapping("/setting")
@RequiredArgsConstructor
public class SettingController {

    private final UserService userService;
    private final ConnectionLogService connectionLogService;

    @GetMapping("/log")
    public String connectionLogDefault(Model model) {
        return connectionLog(0, model);
    }

    //접속 로그 페이지
    @GetMapping("/log/{page}")
    public String connectionLog(@PathVariable(value = "page") int page, Model model) {

        var auth = SecurityContextHolder.getContext().getAuthentication();

        if(auth == null){
            return "redirect:/login";
        }

        var user = userService.principalUser(auth);

        if(user == null) {
            return "redirect:/login";
        }

        var logs = connectionLogService.findAllLogsByUser(page, user);

        int start = Math.max(page-2, 0);

        model.addAttribute("start_page", start);
        model.addAttribute("end_page", Math.min(start+4, logs.getTotalPages()-1));

        model.addAttribute("logs",logs.get().toList());
        model.addAttribute("current_page",page);
        model.addAttribute("total_page", logs.getTotalPages()-1);

        return "connection-log";
    }

    // 정보 수정 페이지
    @GetMapping("/user")
    public String userSetting(Model model){

        var auth = SecurityContextHolder.getContext().getAuthentication();

        if(auth == null){
            return "redirect:/login";
        }

        var user = userService.principalUser(auth);

        if(user == null) {
            return "redirect:/login";
        }

        model.addAttribute("user", user);

        return "modify-user";
    }

    // 정보 수정 요청
    @PostMapping("/modify")
    public String modify(@ModelAttribute ModifyUserForm userForm, Model model){

        var auth = SecurityContextHolder.getContext().getAuthentication();

        if(auth == null){
            return "redirect:/login";
        }

        var user = userService.principalUser(auth);

        if(user == null) {
            return "redirect:/login";
        }

        user.setUsername(userForm.getName());
        user.setBirth(userForm.getBirth());
        user.setPhone(userForm.getTel());
        user.setGender(userForm.getGender());
        user.setAddress(userForm.getAddress());

        userService.updateUser(user);

        model.addAttribute("user", user);

        return "redirect:/main";
    }
}
