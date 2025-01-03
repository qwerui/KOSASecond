package com.app.sketchbook.chat.dto;

import com.app.sketchbook.user.entity.SketchUser;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ChatRoomModel {
    private Long room;
    private String opponent;
    private String profile_img_url;
    private boolean messagesExists;
}
