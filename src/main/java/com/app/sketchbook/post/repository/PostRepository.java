// 작업자 : 이하린

package com.app.sketchbook.post.repository;

import com.app.sketchbook.post.entity.Post;
import com.app.sketchbook.user.entity.SketchUser;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;

public interface PostRepository extends JpaRepository<Post, Integer> {
    Slice<Post> findBySketchUserId(Long user_id, Pageable pageable);
    Slice<Post> findBySketchUserIn(Set<SketchUser> sketchUsers, Pageable pageable);
}