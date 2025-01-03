package com.app.sketchbook.chat.service;

import com.app.sketchbook.chat.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

//작업자 : 홍제기
@Log
@Component
@RequiredArgsConstructor
public class WebSocketEventListener {

    private final ChatRoomService chatRoomService;

    // 웹 소켓 생성시 이벤트, 채팅방이 없으면 생성한다.
    @EventListener
    private void handleSessionConnected(SessionConnectEvent event) {
        StompHeaderAccessor header = StompHeaderAccessor.wrap(event.getMessage());


        String roomHeader = header.getFirstNativeHeader("room");

        if(roomHeader == null) {
            return;
        }

        // 채팅방 생성
        Long roomNo = Long.parseLong(roomHeader);

        if(!chatRoomService.checkRoomCreated(roomNo)){
            chatRoomService.createRoom(roomNo);
        }
    }
}
