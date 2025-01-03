package com.app.sketchbook.chat.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

//작업자 : 홍제기
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic"); // 서버->클라이언트 URI 설정
        config.setApplicationDestinationPrefixes("/app"); //클라이언트->서버 URI 설정
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        //웹 소켓 접속 엔드 포인트
        registry.addEndpoint("/chat-socket").setAllowedOriginPatterns("*");
    }
}