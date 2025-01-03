//작업자 : 한수민

package com.app.sketchbook.user.controller;

import com.app.sketchbook.friend.service.FriendService;
import com.app.sketchbook.post.entity.Post;
import com.app.sketchbook.post.service.PostService;
import com.app.sketchbook.user.entity.SketchUser;
import com.app.sketchbook.user.exception.FileStorageException;
import com.app.sketchbook.user.service.FileStorageService;
import com.app.sketchbook.user.service.MyPageService;
import com.app.sketchbook.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/profile")
public class MyPageController {

    private final UserService userService;
    private final MyPageService myPageService;
    private final FriendService friendService;
    private final FileStorageService fileStorageService;
    private final PostService postService;

    // 마이 페이지 게시글 로드 요청 경로-이하린
    @GetMapping("/getmypost/{pageNumber}")
    public String my_post_list(Model model, @PathVariable int pageNumber) {
        Slice<Post> posts = postService.fetchPostsByPage(pageNumber);
        SketchUser user = userService.principalUser(SecurityContextHolder.getContext().getAuthentication());

        if (posts.hasNext()) {
            model.addAttribute("nextPageNumber", pageNumber + 1);
        }
        model.addAttribute("postList", posts.getContent());
        model.addAttribute("user", user);

        return "mypost";
    }

    //내 프로필 보기
    @GetMapping("")
    public String userProfile(Model model) {
        SketchUser profileOwner = userService.principalUser(SecurityContextHolder.getContext().getAuthentication());
        model.addAttribute("user", profileOwner);
        return "mypage";
    }

    //다른 사용자 프로필 보기
    @GetMapping("/view/{profileOwnerId}")
    public String userProfile(@PathVariable Long profileOwnerId, Model model) {
        try {
            SketchUser user = userService.principalUser(SecurityContextHolder.getContext().getAuthentication());
            SketchUser profileOwner = friendService.getUserProfile(user, profileOwnerId);
            model.addAttribute("user", user);
            model.addAttribute("profileOwner", profileOwner);
            return "profile";
        } catch (AccessDeniedException e) {
            model.addAttribute("error", e.getMessage());
            return "access_denied";
        }
    }

    //프로필 사진 업로드
    @PostMapping("/uploadProfile")
    public String profileUpload(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
        try {
            SketchUser user = userService.principalUser(SecurityContextHolder.getContext().getAuthentication());
            SketchUser profileOwner = friendService.getUserProfile(user, user.getId());
            if(!user.getId().equals(profileOwner.getId())) {
                throw new AccessDeniedException("접근 불가");
            }
            String fileUrl = fileStorageService.storeFile(file, user.getId(), "profile");
            myPageService.uploadProfile(user, fileUrl);
            redirectAttributes.addFlashAttribute("message", "프로필 사진이 성공적으로 업데이트되었습니다.");
        } catch (FileStorageException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/profile";
    }

    //배경 사진 업로드
    @PostMapping("/uploadCover")
    public String coverUpload(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
        try {
            SketchUser user = userService.principalUser(SecurityContextHolder.getContext().getAuthentication());
            SketchUser profileOwner = friendService.getUserProfile(user, user.getId());
            if(!user.getId().equals(profileOwner.getId())) {
                throw new AccessDeniedException("접근 불가");
            }
            String fileUrl = fileStorageService.storeFile(file, user.getId(), "cover");
            myPageService.uploadCover(user, fileUrl);
            redirectAttributes.addFlashAttribute("message", "배경 사진이 성공적으로 업데이트되었습니다.");
        } catch (FileStorageException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/profile";
    }
}