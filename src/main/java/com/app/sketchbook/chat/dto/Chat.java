package com.app.sketchbook.chat.dto;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Chat {
    private String id;
    private String room;
    private String user;
    private String userid;
    private String content;
}
