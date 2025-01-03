package com.app.sketchbook.chat.service;

import com.app.sketchbook.chat.dto.ChatRoomModel;
import com.app.sketchbook.chat.entity.ChatRoom;
import com.app.sketchbook.chat.repository.ChatRoomRepository;
import com.app.sketchbook.friend.entity.Friend;
import com.app.sketchbook.friend.repository.FriendRepository;
import com.app.sketchbook.friend.service.FriendService;
import com.app.sketchbook.user.entity.SketchUser;
import com.app.sketchbook.user.repository.UserRepository;
import com.app.sketchbook.user.service.UserService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

//작업자 : 홍제기
@Log
@Service
@RequiredArgsConstructor
public class ChatRoomServiceImpl implements ChatRoomService{

    private final ChatRoomRepository chatRoomRepository;
    private final FriendService friendService;
    private final UserService userService;
    
    // 채팅방 생성
    @Transactional
    @Override
    public void createRoom(Long friendNo) {
        var room = new ChatRoom();
        var friend = friendService.findById(friendNo);

        if(friend.isEmpty()){
            return;
        }

        room.setId(friendNo);
        room.setFriend(friend.get());

        Date current = new Date();

        room.setFromDisconnection(current);
        room.setToDisconnection(current);
        room.setLastSend(current);

        chatRoomRepository.save(room);
    }
    
    // 채팅방 존재 여부 확인
    @Override
    public boolean checkRoomCreated(Long roomNo) {
        return chatRoomRepository.existsById(roomNo);
    }
    
    // 사용자가 채팅방에서 접속 종료한 시간 기록
    @Transactional
    @Override
    public void updateDisconnectTime(Long room, Authentication auth) {
        var foundRoom = chatRoomRepository.findById(room);

        if(foundRoom.isEmpty()){
            log.info("Empty Room");
            return;
        }

        var friend = foundRoom.get().getFriend();
        var user = userService.principalUser(auth);

        log.info("User ID: "+user.getId());

        if(friend.getFrom().getId().equals(user.getId())){
            foundRoom.get().setFromDisconnection(new Date());
        } else {
            foundRoom.get().setToDisconnection(new Date());
        }
    }
    
    // 채팅방의 마지막 전송시간 업데이트
    @Transactional
    @Override
    public void updateLastSend(Long room, Date time) {
        var foundRoom = chatRoomRepository.findById(room);

        if(foundRoom.isEmpty()) {
            return;
        }

        foundRoom.get().setLastSend(time);
    }
    
    // 현재 접속중인 사용자의 채팅방 리스트를 가져온다.
    @Override
    public List<ChatRoomModel> getChatRoomList() {

        var user = userService.principalUser(SecurityContextHolder.getContext().getAuthentication());

        if(user == null){
            return List.of();
        }

        var allRooms = friendService.getFriends(user);
        var receivedRooms = chatRoomRepository.findAllByIdWithExistsMessage(user);

        List<ChatRoomModel> result = new ArrayList<>();

        for(var room : allRooms){
            ChatRoomModel model = getChatRoomModel(room, user, receivedRooms);

            result.add(model);
        }

        return result;
    }
    
    // 채팅방 정보를 객체에 담음
    private static ChatRoomModel getChatRoomModel(Friend room, SketchUser user, List<Long> receivedRooms) {
        ChatRoomModel model = new ChatRoomModel();

        model.setRoom(room.getNo());

        if(room.getFrom().getId().equals(user.getId())){
            model.setOpponent(room.getTo().getUsername());
            model.setProfile_img_url(room.getTo().getProfile_img_url());
        } else {
            model.setOpponent(room.getFrom().getUsername());
            model.setProfile_img_url(room.getFrom().getProfile_img_url());
        }

        if(receivedRooms.contains(room.getNo())){
            model.setMessagesExists(true);
        }
        return model;
    }
}
