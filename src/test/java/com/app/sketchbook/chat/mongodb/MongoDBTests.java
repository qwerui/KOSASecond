package com.app.sketchbook.chat.mongodb;

import com.app.sketchbook.chat.dto.ReceivedChat;
import com.app.sketchbook.chat.repository.ChatRepository;
import com.app.sketchbook.chat.service.ChatLogService;
import lombok.extern.java.Log;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Log
@SpringBootTest
public class MongoDBTests {

    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private ChatLogService chatLogService;
    @Autowired
    private TestChatRepository chatRepository;

    @Test
    public void testConnection(){
        try {
            String ok = mongoTemplate.getDb().getName();
            System.out.println("MongoDB connection established: " + ok);
        } catch (Exception e) {
            System.out.println("MongoDB connection failed: " + e.getMessage());
        }
    }

    @Test
    public void testInsertChatLog(){

        for(int i=0;i<10000000;i++){
            ReceivedChat chatLog = new ReceivedChat();
            chatLog.setId(""+i);
            chatLog.setRoom((int)(Math.random()*1000)+"");
            chatLog.setUser("test");
            chatLog.setSendTime(new Date());
            chatLog.setContent("my test");
            chatLogService.insertChatLog(chatLog);
        }
    }

    @Test
    public void testGetRecentChats(){
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE,-75);
        Date date = cal.getTime();
        long startTime = System.currentTimeMillis();
        var list = chatRepository.findAllBySendTimeAfter(date);
        log.info("Search Time : "+(System.currentTimeMillis()-startTime));
        log.info(""+list.size());
    }

    @Test
    public void testGetChatsByRoom(){
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MINUTE,-240);
        Date date = cal.getTime();
        long startTime = System.currentTimeMillis();
        var list = chatRepository.findAllByRoomAndSendTimeAfterOrderBySendTimeAsc("1", date);
        log.info("Search Time : "+(System.currentTimeMillis()-startTime));
        log.info(""+list.size());
    }

    @Test
    public void testGetChatCountByRoom() {
        long startTime = System.currentTimeMillis();
        long count = chatRepository.countByRoom("123");
        log.info("Search Time : "+(System.currentTimeMillis()-startTime));
        log.info(""+count);
    }

}
