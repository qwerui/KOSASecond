// 작업자 : 이하린

package com.app.sketchbook.post.controller;

import com.app.sketchbook.chat.service.ChatRoomService;
import com.app.sketchbook.post.DTO.ImageRequestDTO;
import com.app.sketchbook.post.entity.Image;
import com.app.sketchbook.post.entity.Post;
import com.app.sketchbook.post.repository.ImageRepository;
import com.app.sketchbook.post.service.PostService;
import com.app.sketchbook.user.entity.SketchUser;
import com.app.sketchbook.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Slice;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Log4j2
@Controller
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;
    private final ImageRepository imageRepository;
    private final UserService userService;
    private final ChatRoomService chatRoomService;

    @GetMapping("/main")
    public String main(Model model) {
        SketchUser user = userService.principalUser(SecurityContextHolder.getContext().getAuthentication());
        model.addAttribute("user", user);
        var rooms = chatRoomService.getChatRoomList();
        model.addAttribute("rooms", rooms);
        return "main";
    }

    @GetMapping("/mypage")
    public String my(Model model) {
        SketchUser user = userService.principalUser(SecurityContextHolder.getContext().getAuthentication());
        model.addAttribute("user", user);

        return "my";
    }

    // 메인 페이지 게시글 로드 요청 경로
    @GetMapping("/getpost/{pageNumber}")
    public String main_post_list(Model model, @PathVariable int pageNumber) {
        Slice<Post> posts = postService.fetchPostsByPageAndFriendStatus(pageNumber);
        SketchUser user = userService.principalUser(SecurityContextHolder.getContext().getAuthentication());

        if (posts.hasNext()) {
            model.addAttribute("nextPageNumber", pageNumber + 1);
        }

        model.addAttribute("postList", posts.getContent());
        model.addAttribute("user", user);

        return "post";
    }

    // 마이 페이지 게시글 로드 요청 경로
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
    @PostMapping("/mypost/create")
    public String create_mypost(Post post, @RequestParam("imageData") String imageDataList) {
        // Post 엔티티에 저장
        SketchUser user = userService.principalUser(SecurityContextHolder.getContext().getAuthentication());
        Post savedPost = postService.create_post(post, user);

        String[] imageDataArray = imageDataList.split("base64,");

        for (int i = 1; i < imageDataArray.length; i++) {
            String imageData = imageDataArray[i]; // base64 데이터만 가져옴
            if (imageData != null && !imageData.isEmpty()) {
                try {
                    // 공백 및 모든 공백 문자 제거
                    imageData = imageData.replaceAll("\\s+", "");

                    // 이미지 데이터 저장
                    postService.saveImage(savedPost, imageData);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return "redirect:/profile";
    }

    @PostMapping("/post/create")
    public String create_post(Post post, @RequestParam("imageData") String imageDataList) {
        // Post 엔티티에 저장
        SketchUser user = userService.principalUser(SecurityContextHolder.getContext().getAuthentication());
        Post savedPost = postService.create_post(post, user);

        String[] imageDataArray = imageDataList.split("base64,");

        for (int i = 1; i < imageDataArray.length; i++) {
            String imageData = imageDataArray[i]; // base64 데이터만 가져옴
            if (imageData != null && !imageData.isEmpty()) {
                try {
                    // 공백 및 모든 공백 문자 제거
                    imageData = imageData.replaceAll("\\s+", "");

                    // 이미지 데이터 저장
                    postService.saveImage(savedPost, imageData);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return "redirect:/main";
    }

    @PostMapping("/post/delete/{no}")
    public ResponseEntity<?> delete_post(@PathVariable Long no) {
        if (no != null) {
            postService.delete_post(no);
            return ResponseEntity.ok().body("{\"success\": true}");
        }
        return ResponseEntity.status(400).body("{\"success\": false}");
    }

    // 게시글 내용 수정 및 추가 이미지 저장
    @PostMapping("/post/modify/{no}")
    public String modify_post(@PathVariable Long no, @RequestParam("content") String content,
                                                     @RequestParam("postImageData") String imageDataList) {
        // Post 엔티티에 저장
        Post modifiedPost = postService.modify_post(no, content);

        String[] imageDataArray = imageDataList.split("base64,");

        for (int i = 1; i < imageDataArray.length; i++) {
            String imageData = imageDataArray[i]; // base64 데이터만 가져옴
            if (imageData != null && !imageData.isEmpty()) {
                try {
                    // 공백 및 모든 공백 문자 제거
                    imageData = imageData.replaceAll("\\s+", "");

                    // 이미지 데이터 저장
                    postService.saveImage(modifiedPost, imageData);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return "redirect:/main";
    }

    // 이미지 리스트만 삭제
    @PostMapping("/images/delete")
    public ResponseEntity<String> deleteImages(@RequestBody ImageRequestDTO imageRequestDTO) {
        List<Long> selectedImageIds = imageRequestDTO.getSelectedImageIds();

        if (selectedImageIds != null && !selectedImageIds.isEmpty()) {
            selectedImageIds.forEach(id -> {
                Optional<Image> optionalImage = imageRepository.findById(id);
                optionalImage.ifPresent(image -> {
                    image.set_deleted(true);
                    imageRepository.save(image);
                });
            });

            return ResponseEntity.ok().body("{\"success\": true}");
        } else {
            return ResponseEntity.status(400).body("{\"success\": false}");
        }
    }

    @PostMapping("/post/like/{no}")
    public ResponseEntity<?> like_post(@PathVariable Long no) {
        SketchUser user = userService.principalUser(SecurityContextHolder.getContext().getAuthentication());

        if (no != null) {
            postService.like_post(no, user);
            return ResponseEntity.ok().body("{\"success\": true}");
        }
        return ResponseEntity.status(400).body("{\"success\": false}");
    }

    @PostMapping("/post/cancel-like/{no}")
    public ResponseEntity<?> cancel_like_post(@PathVariable Long no) {
//      user = userRepository.getReferenceById(1L); // post/cancel-like/1 요청시 1번 게시글 좋아요의 1번 사용자 좋아요를 취소
        SketchUser user = userService.principalUser(SecurityContextHolder.getContext().getAuthentication());

        if (no != null) {
            postService.cancel_post_like(no, user);
            return ResponseEntity.ok().body("{\"success\": true}");
        }
        return ResponseEntity.status(400).body("{\"success\": false}");
    }
}