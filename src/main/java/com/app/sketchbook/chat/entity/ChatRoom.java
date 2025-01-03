package com.app.sketchbook.chat.entity;

import com.app.sketchbook.friend.entity.Friend;
import com.app.sketchbook.user.entity.SketchUser;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;

import java.util.Date;

@Getter
@Setter
@Entity
public class ChatRoom {
    @Id
    private Long id;

    @OneToOne
    @MapsId
    @JoinColumn(referencedColumnName = "no")
    private Friend friend;

    private Date fromDisconnection;
    private Date toDisconnection;
    private Date lastSend;
}
