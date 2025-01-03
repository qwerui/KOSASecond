package com.app.sketchbook.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReceivedChat extends Chat{
    private Date sendTime;

    public ReceivedChat(Chat chat, Date sendTime){
        setRoom(chat.getRoom());
        setUser(chat.getUser());
        setContent(chat.getContent());
        setSendTime(sendTime);
        setId(chat.getId());
        setUserid(chat.getUserid());
    }
}
