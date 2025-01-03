package com.app.sketchbook.chat.service;

import com.app.sketchbook.chat.dto.Chat;
import com.app.sketchbook.chat.dto.ReceivedChat;
import com.mongodb.DuplicateKeyException;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.Date;

//작업자 : 홍제기
@Log
@Component
@RequiredArgsConstructor
public class KafkaChatListener {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatLogService chatLogService;
    private final ChatRoomService chatRoomService;
    private final ChatNotifyService chatNotifyService;

    //Kafka의 메시지 수신 메소드
    @KafkaListener(topics = "chat", groupId = "chat-group", containerFactory = "kafkaChatContainerFactory", autoStartup = "true")
    public void listen(Chat chat) {
        ReceivedChat receivedChat = new ReceivedChat(chat, new Date());

        // Kafka에서 메시지를 받아서 WebSocket을 통해 클라이언트에게 전송
        messagingTemplate.convertAndSend("/topic/receive/"+chat.getRoom(), receivedChat);

        // 메시지 수신 시 알림 전송
        chatNotifyService.notifyChat(receivedChat.getRoom(), receivedChat.getUserid());

        // MongoDB에 저장
        try{
            chatLogService.insertChatLog(receivedChat);
        } catch (DuplicateKeyException ignored){
            // 중복 키 존재할 때 발생하기 때문에 예외를 무시
            // MongoDB에 중복 데이터가 발생하지 않도록 한 조치
        } catch (Exception e){
            e.printStackTrace();
        }

        // 채팅방 정보 갱신
        chatRoomService.updateLastSend(Long.parseLong(receivedChat.getRoom()), receivedChat.getSendTime());
    }
}