package com.app.sketchbook.chat.service;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

//작업자 : 홍제기
public interface ChatNotifyService{
    void notifyChat(String room, String sender);
    void addEmitter(Long user, SseEmitter emitter);
}
