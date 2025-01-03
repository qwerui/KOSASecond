//작업자 : 한수민

package com.app.sketchbook.friend.entity;

import com.app.sketchbook.user.entity.SketchUser;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class Friend {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long no;

    @ManyToOne
    private SketchUser from;

    @ManyToOne
    private SketchUser to;

    @Enumerated(EnumType.STRING)
    private FriendStatus status;
}