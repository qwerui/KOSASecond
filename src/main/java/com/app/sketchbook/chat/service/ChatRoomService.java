package com.app.sketchbook.chat.service;

import com.app.sketchbook.chat.dto.ChatRoomModel;
import com.app.sketchbook.chat.entity.ChatRoom;
import org.springframework.security.core.Authentication;

import java.util.Date;
import java.util.List;

//작업자 : 홍제기
public interface ChatRoomService {

    void createRoom(Long friendNo);
    boolean checkRoomCreated(Long roomNo);
    void updateDisconnectTime(Long room, Authentication auth);
    void updateLastSend(Long room, Date time);
    List<ChatRoomModel> getChatRoomList();
}
