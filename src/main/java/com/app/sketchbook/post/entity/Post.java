// 작업자 : 이하린

package com.app.sketchbook.post.entity;

import com.app.sketchbook.reply.entity.Reply;
import com.app.sketchbook.user.entity.SketchUser;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@Where(clause = "is_deleted = false")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long no;

    @Column(length = 4000)
    private String content;

    private LocalDateTime created_date;

    private LocalDateTime modified_date;

    @Column(columnDefinition = "number default 0")
    private boolean is_deleted;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Image> image_list = new ArrayList<>();

    @OneToMany(mappedBy = "post", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @OrderBy("no asc") // 댓글 정렬
    private List<Reply> reply_list;

    @ManyToOne
    @JoinColumn(name = "id")
    private SketchUser sketchUser;

    @ManyToMany
    @JoinTable(
            name = "post_like",
            joinColumns = @JoinColumn(name = "no"),
            inverseJoinColumns = @JoinColumn(name = "like_id"))
    private Set<SketchUser> like;
}
