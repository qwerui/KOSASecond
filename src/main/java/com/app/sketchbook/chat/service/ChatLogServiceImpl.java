package com.app.sketchbook.chat.service;

import com.app.sketchbook.chat.entity.ChatLog;
import com.app.sketchbook.chat.dto.ReceivedChat;
import com.app.sketchbook.chat.repository.ChatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

//작업자 : 홍제기
@Service
@RequiredArgsConstructor
public class ChatLogServiceImpl implements ChatLogService {
    private final ChatRepository chatRepository;

    // 채팅 로그 저장
    @Override
    public void insertChatLog(ReceivedChat chat) {
        ChatLog chatLog = new ChatLog(chat);
        chatRepository.save(chatLog);
    }

    // 최근 채팅 로그 불러오기, 최대 7일전으로 설정
    @Override
    public List<ChatLog> getRecentLogs(String room) {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -7);
        Date startDate = cal.getTime();
        return chatRepository.findAllByRoomAndSendTimeAfterOrderBySendTimeAsc(room, startDate);
    }
}
