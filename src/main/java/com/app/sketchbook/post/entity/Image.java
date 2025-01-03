// 작업자 : 이하린

package com.app.sketchbook.post.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Where;

@Entity
@Getter
@Setter
@Where(clause = "is_deleted = false")
public class Image {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long no;

    @ManyToOne
    private Post post;

    private String file_path;

    @Column(columnDefinition = "number default 0")
    private boolean is_deleted;
}