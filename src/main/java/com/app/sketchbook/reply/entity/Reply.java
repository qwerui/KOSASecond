// 작업자 : 이하린

package com.app.sketchbook.reply.entity;

import com.app.sketchbook.post.entity.Post;
import com.app.sketchbook.user.entity.SketchUser;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Getter
@Setter
@Where(clause = "is_deleted = false")
public class Reply {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long no;

    @Column(length = 4000)
    private String content;

    private LocalDateTime created_date;

    private LocalDateTime modified_date;

    @Column(columnDefinition = "number default 0")
    private boolean is_deleted;

    @ManyToOne
    private Post post;

    @ManyToOne
    @JoinColumn(name = "id")
    private SketchUser user;

    @ManyToMany
    private Set<SketchUser> like;
}