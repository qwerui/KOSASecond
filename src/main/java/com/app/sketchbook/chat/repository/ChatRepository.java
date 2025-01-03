package com.app.sketchbook.chat.repository;

import com.app.sketchbook.chat.entity.ChatLog;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Date;
import java.util.List;

//작업자 : 홍제기
public interface ChatRepository extends MongoRepository<ChatLog,String> {
    //채팅 방을 기준으로, 일정 시간 이후의 채팅 기록을 가져옴
    public List<ChatLog> findAllByRoomAndSendTimeAfterOrderBySendTimeAsc(String room, Date startDate);
    public ChatLog findByRoomOrderBySendTimeDesc(String room);
}
