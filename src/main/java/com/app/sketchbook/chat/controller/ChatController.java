package com.app.sketchbook.chat.controller;

import com.app.sketchbook.SecurityConfig;
import com.app.sketchbook.chat.dto.Chat;
import com.app.sketchbook.chat.entity.ChatLog;
import com.app.sketchbook.chat.service.ChatLogService;
import com.app.sketchbook.chat.service.ChatRoomService;
import com.app.sketchbook.chat.service.KafkaChatProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

//작업자 : 홍제기
@Log
@Controller
@RequiredArgsConstructor
public class ChatController {

    private final KafkaChatProducer producer;
    private final ChatLogService chatLogService;
    private final ChatRoomService chatRoomService;

    @MessageMapping("/send")
    public void sendMessage(Chat chat) {
        producer.sendMessage(chat); // Kafka에 메시지 전송
    }

    @MessageMapping("/history/{room}")
    @SendTo("/topic/history/{room}")
    public List<ChatLog> fetchChatLog(@DestinationVariable String room){
        // MongoDB에서 기록 얻어오기
        return chatLogService.getRecentLogs(room);
    }

    @GetMapping("/disconnect/{room}")
    @ResponseBody
    public void updateDisconnectTime(@PathVariable("room") Long room){
        chatRoomService.updateDisconnectTime(room, SecurityContextHolder.getContext().getAuthentication());
    }

}