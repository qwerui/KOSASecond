package com.app.sketchbook.chat.jpa;

import com.app.sketchbook.chat.entity.ChatRoom;
import com.app.sketchbook.chat.repository.ChatRoomRepository;
import com.app.sketchbook.user.entity.SketchUser;
import jakarta.transaction.Transactional;
import lombok.extern.java.Log;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Log
@SpringBootTest
public class JPATests {

    @Autowired
    ChatRoomRepository chatRoomRepository;
//
//    @Test
//    @Transactional
//    public void testGetUsers(){
//        var room = new ChatRoom();
//        room.setRequester(new SketchUser());
//        room.setReceiver(new SketchUser());
//        chatRoomRepository.save(room);
//
//        var result = chatRoomRepository.findById(7L);
//        assertFalse(result.isEmpty());
//    }

    @Test
    @Transactional
    public void testGetUsers(){

        var result = chatRoomRepository.findById(1L);
        assertFalse(result.isEmpty());
    }

}
