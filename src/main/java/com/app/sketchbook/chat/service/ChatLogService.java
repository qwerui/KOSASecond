package com.app.sketchbook.chat.service;

import com.app.sketchbook.chat.entity.ChatLog;
import com.app.sketchbook.chat.dto.ReceivedChat;

import java.util.Date;
import java.util.List;

//작업자 : 홍제기
public interface ChatLogService {
    public void insertChatLog(ReceivedChat chat);
    public List<ChatLog> getRecentLogs(String room);
}
