package com.app.sketchbook.chat.mongodb;

import com.app.sketchbook.chat.entity.ChatLog;
import com.app.sketchbook.chat.repository.ChatRepository;

import java.util.Date;
import java.util.List;

public interface TestChatRepository extends ChatRepository {
    public List<ChatLog> findAllBySendTimeAfter(Date startDate);
    public List<ChatLog> findAllByRoom(String room);
    public long countByRoom(String room);
}
