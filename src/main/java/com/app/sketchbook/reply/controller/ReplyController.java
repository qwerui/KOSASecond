// 작업자 : 이하린

package com.app.sketchbook.reply.controller;

import com.app.sketchbook.post.entity.Post;
import com.app.sketchbook.post.service.PostService;
import com.app.sketchbook.reply.entity.Reply;
import com.app.sketchbook.reply.service.ReplyService;
import com.app.sketchbook.user.entity.SketchUser;
import com.app.sketchbook.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/reply")
@RequiredArgsConstructor
@Controller
public class ReplyController {
    private final PostService postService;
    private final ReplyService replyService;
    private final UserService userService;

    @PostMapping("/create/{no}")
    public String create_reply(Model model, @PathVariable("no") Integer no, String content) {
        Post post = postService.getPost(no);
        replyService.reply_create(post, content);

        model.addAttribute("post", post);

        return "redirect:/main";
    }

    @PostMapping("/mycreate/{no}")
    public String mycreate_reply(Model model, @PathVariable("no") Integer no, String content) {
        Post post = postService.getPost(no);
        replyService.reply_create(post, content);

        model.addAttribute("post", post);

        return "redirect:/profile";
    }

    @PostMapping("/modify/{no}")
    public ResponseEntity<?> modify_reply(@PathVariable("no") Integer no, @RequestBody Reply modifiedReply) {
        Reply reply = replyService.getReply(no);
        if (reply != null) {
            reply.setContent(modifiedReply.getContent());
            replyService.reply_modify(reply);
            return ResponseEntity.ok().body("{\"success\": true}");
        }
        return ResponseEntity.status(400).body("{\"success\": false}");
    }

    @PostMapping("/delete/{no}")
    public ResponseEntity<?> delete_reply(@PathVariable("no") Integer no) {
        Reply reply = replyService.getReply(no);
        if (reply != null) {
            replyService.reply_delete(reply);
            return ResponseEntity.ok().body("{\"success\": true}");
        }
        return ResponseEntity.status(400).body("{\"success\": false}");
    }

    @PostMapping("/like/{no}")
    public ResponseEntity<?> like_reply(@PathVariable Long no) {
        SketchUser user = userService.principalUser(SecurityContextHolder.getContext().getAuthentication());

        if (no != null) {
            replyService.like_reply(no, user);
            return ResponseEntity.ok().body("{\"success\": true}");
        }
        return ResponseEntity.status(400).body("{\"success\": false}");
    }

    @PostMapping("/cancel-like/{no}")
    public ResponseEntity<?> cancel_like_reply(@PathVariable Long no) {
        SketchUser user = userService.principalUser(SecurityContextHolder.getContext().getAuthentication());

        if (no != null) {
            replyService.cancel_reply_like(no, user);
            return ResponseEntity.ok().body("{\"success\": true}");
        }
        return ResponseEntity.status(400).body("{\"success\": false}");
    }
}