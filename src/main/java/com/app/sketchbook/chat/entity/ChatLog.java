package com.app.sketchbook.chat.entity;

import com.app.sketchbook.chat.dto.ReceivedChat;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Document(collection = "test")
@Getter
@Setter
@NoArgsConstructor
public class ChatLog {
    @Id
    private String id;
    private String room;
    private String user;
    private String content;

    private Date sendTime;

    public ChatLog(ReceivedChat receivedChat){
        id = receivedChat.getId();
        room = receivedChat.getRoom();
        user = receivedChat.getUser();
        content = receivedChat.getContent();
        sendTime = receivedChat.getSendTime();
    }
}
