// 작업자 : 이하린

package com.app.sketchbook.reply.service;

import com.app.sketchbook.post.entity.Post;
import com.app.sketchbook.reply.entity.Reply;
import com.app.sketchbook.reply.repository.ReplyRepository;
import com.app.sketchbook.user.entity.SketchUser;
import com.app.sketchbook.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ReplyService {
    private final ReplyRepository replyRepository;
    private final UserService userService;

    public void reply_create(Post post, String content) {
        Reply reply = new Reply();
        SketchUser id = userService.principalUser(SecurityContextHolder.getContext().getAuthentication());
        reply.setContent(content);
        reply.setCreated_date(LocalDateTime.now());
        reply.setPost(post);
        reply.setUser(id);
        replyRepository.save(reply);
    }

    public void reply_modify(Reply reply) {
        reply.setModified_date(LocalDateTime.now());
        replyRepository.save(reply);
    }

    public Reply getReply(Integer no) {
        Optional<Reply> reply = replyRepository.findById(no);
        return reply.orElse(null);
    }

    public void reply_delete(Reply reply) {
        reply.set_deleted(true);
        replyRepository.save(reply);
    }

    public void like_reply(Long no, SketchUser user) { // 임시
        Reply reply = replyRepository.getReferenceById(no.intValue()); // 범위에 대한 예외처리 필요
        reply.getLike().add(user);
        replyRepository.save(reply);
    }

    public void cancel_reply_like(Long no, SketchUser user) {
        Reply reply = replyRepository.getReferenceById(no.intValue());
        Set<SketchUser> likedUser = reply.getLike();
        likedUser.remove(user);
        reply.setLike(likedUser);
        replyRepository.save(reply);
    }
}